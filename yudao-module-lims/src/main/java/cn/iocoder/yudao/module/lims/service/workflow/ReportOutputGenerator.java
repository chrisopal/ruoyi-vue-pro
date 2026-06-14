package cn.iocoder.yudao.module.lims.service.workflow;

import cn.iocoder.yudao.module.lims.service.workflow.model.ReportOutputArtifact;
import cn.iocoder.yudao.module.lims.service.workflow.model.ReportOutputBundle;
import cn.iocoder.yudao.module.lims.service.workflow.model.ReportOutputRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class ReportOutputGenerator {

    public static final String OUTPUT_DIR_PROPERTY = "lims.report.output.dir";

    private static final Path DEFAULT_OUTPUT_DIR = Path.of("target", "lims-report-output");
    private static final String URL_PREFIX = "/lims/report-output";

    private final ObjectMapper objectMapper;

    public ReportOutputGenerator(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public ReportOutputBundle generate(ReportOutputRequest request) {
        List<String> formats = resolveOutputFormats(request.reportDraftPlan());
        List<String> reportLines = buildReportLines(request);
        String reportNo = safeSegment(request.reportNo());
        Path directory = outputRoot().resolve(reportNo);
        try {
            Files.createDirectories(directory);
            List<ReportOutputArtifact> artifacts = new ArrayList<>();
            for (String format : formats) {
                GeneratedOutput output = createOutput(format, reportNo, reportLines);
                Path file = directory.resolve(output.fileName());
                Files.write(file, output.content());
                artifacts.add(new ReportOutputArtifact(
                        output.format(),
                        output.fileName(),
                        URL_PREFIX + "/" + reportNo + "/" + output.fileName(),
                        sha256(output.content()),
                        request.generatedAt()));
            }
            ReportOutputArtifact primary = selectPrimary(artifacts);
            return new ReportOutputBundle(primary.format(), primary.fileUrl(), request.generatedAt(), artifacts);
        } catch (IOException ex) {
            throw new IllegalStateException("Unable to generate LIMS report outputs", ex);
        }
    }

    private List<String> resolveOutputFormats(JsonNode reportDraftPlan) {
        Set<String> formats = new LinkedHashSet<>();
        JsonNode configured = reportDraftPlan == null ? null : reportDraftPlan.path("outputFormats");
        if (configured != null && configured.isArray()) {
            configured.forEach(node -> {
                String format = normalizeFormat(node.asText());
                if (StringUtils.hasText(format)) {
                    formats.add(format);
                }
            });
        }
        if (formats.isEmpty()) {
            formats.add("PDF");
        }
        return new ArrayList<>(formats);
    }

    private String normalizeFormat(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        String format = value.trim().toUpperCase(Locale.ROOT);
        return switch (format) {
            case "WORD", "DOCX" -> "WORD";
            case "PDF" -> "PDF";
            case "EXCEL", "XLSX" -> "EXCEL";
            default -> throw new IllegalArgumentException("Unsupported report output format: " + value);
        };
    }

    private GeneratedOutput createOutput(String format, String reportNo, List<String> reportLines) throws IOException {
        return switch (format) {
            case "WORD" -> new GeneratedOutput(format, reportNo + ".docx", createDocx(reportLines));
            case "PDF" -> new GeneratedOutput(format, reportNo + ".pdf", createPdf(reportLines));
            case "EXCEL" -> new GeneratedOutput(format, reportNo + ".xlsx", createXlsx(reportLines));
            default -> throw new IllegalArgumentException("Unsupported report output format: " + format);
        };
    }

    private ReportOutputArtifact selectPrimary(List<ReportOutputArtifact> artifacts) {
        return artifacts.stream()
                .filter(artifact -> "PDF".equals(artifact.format()))
                .findFirst()
                .orElseGet(() -> artifacts.get(0));
    }

    private List<String> buildReportLines(ReportOutputRequest request) {
        JsonNode content = readObject(request.reportContent());
        List<String> lines = new ArrayList<>();
        lines.add(defaultText(request.reportName(), request.reportNo()));
        lines.add("Report No: " + defaultText(request.reportNo(), "-"));
        lines.add("Request No: " + content.path("requestNo").asText("-"));
        lines.add("Request Name: " + content.path("requestName").asText("-"));
        lines.add("Domain Pack: " + content.path("domainPackCode").asText("-")
                + " / " + content.path("domainPackVersion").asText("-"));
        lines.add("Workflow Snapshot Hash: " + content.path("workflowSnapshotHash").asText("-"));
        lines.add("Conclusion: " + defaultText(request.conclusion(), "-"));
        appendResultLines(lines, content);
        appendResultValueLines(lines, content);
        appendTaskLines(lines, content);
        return lines;
    }

    private void appendResultLines(List<String> lines, JsonNode content) {
        JsonNode results = content.path("results");
        if (!results.isArray() || results.isEmpty()) {
            return;
        }
        lines.add("");
        lines.add("Results");
        for (JsonNode result : results) {
            lines.add(result.path("sampleNo").asText("-") + " / "
                    + result.path("testItem").asText("-") + ": "
                    + result.path("resultValue").asText("-")
                    + result.path("resultUnit").asText("")
                    + " / " + result.path("conclusion").asText("-"));
        }
    }

    private void appendResultValueLines(List<String> lines, JsonNode content) {
        JsonNode values = content.path("resultValues");
        if (!values.isArray() || values.isEmpty()) {
            return;
        }
        lines.add("");
        lines.add("Structured Result Values");
        for (JsonNode value : values) {
            lines.add(value.path("taskNo").asText("-") + " / "
                    + value.path("fieldName").asText(value.path("fieldCode").asText("-")) + ": "
                    + value.path("displayValue").asText("-")
                    + " / " + value.path("conclusion").asText("-"));
        }
    }

    private void appendTaskLines(List<String> lines, JsonNode content) {
        JsonNode tasks = content.path("tasks");
        if (!tasks.isArray() || tasks.isEmpty()) {
            return;
        }
        lines.add("");
        lines.add("Tasks and Equipment");
        for (JsonNode task : tasks) {
            lines.add(task.path("taskNo").asText("-") + " / "
                    + task.path("testItem").asText("-") + " / "
                    + task.path("equipmentCode").asText("-") + " "
                    + task.path("equipmentName").asText(""));
        }
    }

    private byte[] createDocx(List<String> lines) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (ZipOutputStream zip = new ZipOutputStream(output, StandardCharsets.UTF_8)) {
            writeZipEntry(zip, "[Content_Types].xml", """
                    <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
                    <Types xmlns="http://schemas.openxmlformats.org/package/2006/content-types">
                      <Default Extension="rels" ContentType="application/vnd.openxmlformats-package.relationships+xml"/>
                      <Default Extension="xml" ContentType="application/xml"/>
                      <Override PartName="/word/document.xml" ContentType="application/vnd.openxmlformats-officedocument.wordprocessingml.document.main+xml"/>
                    </Types>
                    """);
            writeZipEntry(zip, "_rels/.rels", """
                    <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
                    <Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships">
                      <Relationship Id="rId1" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument" Target="word/document.xml"/>
                    </Relationships>
                    """);
            writeZipEntry(zip, "word/document.xml", createWordDocumentXml(lines));
        }
        return output.toByteArray();
    }

    private String createWordDocumentXml(List<String> lines) {
        StringBuilder xml = new StringBuilder("""
                <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
                <w:document xmlns:w="http://schemas.openxmlformats.org/wordprocessingml/2006/main">
                  <w:body>
                """);
        for (String line : lines) {
            xml.append("<w:p><w:r><w:t xml:space=\"preserve\">")
                    .append(xmlEscape(line))
                    .append("</w:t></w:r></w:p>");
        }
        xml.append("""
                    <w:sectPr><w:pgSz w:w="11906" w:h="16838"/><w:pgMar w:top="1440" w:right="1440" w:bottom="1440" w:left="1440"/></w:sectPr>
                  </w:body>
                </w:document>
                """);
        return xml.toString();
    }

    private byte[] createXlsx(List<String> lines) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (ZipOutputStream zip = new ZipOutputStream(output, StandardCharsets.UTF_8)) {
            writeZipEntry(zip, "[Content_Types].xml", """
                    <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
                    <Types xmlns="http://schemas.openxmlformats.org/package/2006/content-types">
                      <Default Extension="rels" ContentType="application/vnd.openxmlformats-package.relationships+xml"/>
                      <Default Extension="xml" ContentType="application/xml"/>
                      <Override PartName="/xl/workbook.xml" ContentType="application/vnd.openxmlformats-officedocument.spreadsheetml.sheet.main+xml"/>
                      <Override PartName="/xl/worksheets/sheet1.xml" ContentType="application/vnd.openxmlformats-officedocument.spreadsheetml.worksheet+xml"/>
                    </Types>
                    """);
            writeZipEntry(zip, "_rels/.rels", """
                    <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
                    <Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships">
                      <Relationship Id="rId1" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument" Target="xl/workbook.xml"/>
                    </Relationships>
                    """);
            writeZipEntry(zip, "xl/workbook.xml", """
                    <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
                    <workbook xmlns="http://schemas.openxmlformats.org/spreadsheetml/2006/main" xmlns:r="http://schemas.openxmlformats.org/officeDocument/2006/relationships">
                      <sheets><sheet name="Report" sheetId="1" r:id="rId1"/></sheets>
                    </workbook>
                    """);
            writeZipEntry(zip, "xl/_rels/workbook.xml.rels", """
                    <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
                    <Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships">
                      <Relationship Id="rId1" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/worksheet" Target="worksheets/sheet1.xml"/>
                    </Relationships>
                    """);
            writeZipEntry(zip, "xl/worksheets/sheet1.xml", createWorksheetXml(lines));
        }
        return output.toByteArray();
    }

    private String createWorksheetXml(List<String> lines) {
        StringBuilder xml = new StringBuilder("""
                <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
                <worksheet xmlns="http://schemas.openxmlformats.org/spreadsheetml/2006/main">
                  <sheetData>
                    <row r="1">
                      <c r="A1" t="inlineStr"><is><t>Field</t></is></c>
                      <c r="B1" t="inlineStr"><is><t>Value</t></is></c>
                    </row>
                """);
        for (int i = 0; i < lines.size(); i++) {
            int row = i + 2;
            String line = lines.get(i);
            String field = line;
            String value = "";
            int split = line.indexOf(':');
            if (split > -1) {
                field = line.substring(0, split);
                value = line.substring(split + 1).trim();
            }
            xml.append("<row r=\"").append(row).append("\">")
                    .append("<c r=\"A").append(row).append("\" t=\"inlineStr\"><is><t>")
                    .append(xmlEscape(field)).append("</t></is></c>")
                    .append("<c r=\"B").append(row).append("\" t=\"inlineStr\"><is><t>")
                    .append(xmlEscape(value)).append("</t></is></c>")
                    .append("</row>");
        }
        xml.append("""
                  </sheetData>
                </worksheet>
                """);
        return xml.toString();
    }

    private byte[] createPdf(List<String> lines) {
        StringBuilder content = new StringBuilder("BT\n/F1 11 Tf\n50 790 Td\n");
        for (String line : lines.stream().limit(42).toList()) {
            content.append("(").append(pdfEscape(toPdfText(line))).append(") Tj\n0 -16 Td\n");
        }
        content.append("ET\n");
        byte[] stream = content.toString().getBytes(StandardCharsets.ISO_8859_1);
        List<byte[]> objects = List.of(
                pdfBytes("<< /Type /Catalog /Pages 2 0 R >>\n"),
                pdfBytes("<< /Type /Pages /Kids [3 0 R] /Count 1 >>\n"),
                pdfBytes("<< /Type /Page /Parent 2 0 R /MediaBox [0 0 595 842] /Resources << /Font << /F1 4 0 R >> >> /Contents 5 0 R >>\n"),
                pdfBytes("<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica >>\n"),
                concat(pdfBytes("<< /Length " + stream.length + " >>\nstream\n"), stream, pdfBytes("endstream\n")));
        return buildPdf(objects);
    }

    private byte[] buildPdf(List<byte[]> objects) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        writeBytes(output, pdfBytes("%PDF-1.4\n"));
        int[] offsets = new int[objects.size() + 1];
        for (int i = 0; i < objects.size(); i++) {
            offsets[i + 1] = output.size();
            writeBytes(output, pdfBytes((i + 1) + " 0 obj\n"));
            writeBytes(output, objects.get(i));
            writeBytes(output, pdfBytes("endobj\n"));
        }
        int xrefOffset = output.size();
        writeBytes(output, pdfBytes("xref\n0 " + (objects.size() + 1) + "\n"));
        writeBytes(output, pdfBytes("0000000000 65535 f \n"));
        for (int i = 1; i < offsets.length; i++) {
            writeBytes(output, pdfBytes(String.format(Locale.ROOT, "%010d 00000 n \n", offsets[i])));
        }
        writeBytes(output, pdfBytes("trailer\n<< /Size " + (objects.size() + 1) + " /Root 1 0 R >>\nstartxref\n" + xrefOffset + "\n%%EOF"));
        return output.toByteArray();
    }

    private void writeZipEntry(ZipOutputStream zip, String name, String content) throws IOException {
        zip.putNextEntry(new ZipEntry(name));
        zip.write(content.getBytes(StandardCharsets.UTF_8));
        zip.closeEntry();
    }

    private void writeBytes(ByteArrayOutputStream output, byte[] bytes) {
        output.write(bytes, 0, bytes.length);
    }

    private byte[] concat(byte[]... values) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        for (byte[] value : values) {
            writeBytes(output, value);
        }
        return output.toByteArray();
    }

    private byte[] pdfBytes(String value) {
        return value.getBytes(StandardCharsets.ISO_8859_1);
    }

    private String toPdfText(String value) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < value.length(); i++) {
            char ch = value.charAt(i);
            builder.append(ch >= 32 && ch <= 126 ? ch : '?');
        }
        return builder.toString();
    }

    private String pdfEscape(String value) {
        return value.replace("\\", "\\\\").replace("(", "\\(").replace(")", "\\)");
    }

    private String xmlEscape(String value) {
        return defaultText(value, "")
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }

    private JsonNode readObject(String json) {
        if (!StringUtils.hasText(json)) {
            return objectMapper.createObjectNode();
        }
        try {
            return objectMapper.readTree(json);
        } catch (JsonProcessingException ex) {
            return objectMapper.createObjectNode();
        }
    }

    private Path outputRoot() {
        String configured = System.getProperty(OUTPUT_DIR_PROPERTY);
        return StringUtils.hasText(configured) ? Path.of(configured) : DEFAULT_OUTPUT_DIR;
    }

    private String safeSegment(String value) {
        String segment = defaultText(value, "report").replaceAll("[^A-Za-z0-9._-]", "_");
        return StringUtils.hasText(segment) ? segment : "report";
    }

    private String defaultText(String value, String fallback) {
        return StringUtils.hasText(value) ? value : fallback;
    }

    private String sha256(byte[] value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(value == null ? new byte[0] : value));
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("SHA-256 algorithm is unavailable", ex);
        }
    }

    private record GeneratedOutput(String format, String fileName, byte[] content) {
    }

}
