package cn.iocoder.yudao.module.lims.service.workflow;

import cn.iocoder.yudao.module.lab.service.domainpack.dto.LabDomainPackSnapshotDTO;
import cn.iocoder.yudao.module.lims.service.workflow.model.WorkflowSnapshot;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HexFormat;

@Service
public class WorkflowSnapshotFactory {

    private final ObjectMapper objectMapper;

    public WorkflowSnapshotFactory(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public WorkflowSnapshot createSnapshot(LabDomainPackSnapshotDTO pack, String overrideConfig) {
        LocalDateTime frozenAt = LocalDateTime.now();
        ObjectNode root = objectMapper.createObjectNode();
        root.put("domainPackId", pack.getDomainPackId());
        root.put("packCode", pack.getPackCode());
        root.put("packName", pack.getPackName());
        root.put("packVersion", pack.getPackVersion());
        root.put("industry", pack.getIndustry());
        root.put("frozenAt", frozenAt.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        root.set("workflow", readObject(pack.getWorkflowSchema()));
        root.set("template", readObject(pack.getTemplateSchema()));
        root.set("workflowNodes", objectMapper.valueToTree(pack.getWorkflowNodes()));
        root.set("sampleRequirements", objectMapper.valueToTree(pack.getSampleRequirements()));
        root.set("testItems", objectMapper.valueToTree(pack.getTestItems()));
        root.set("resultFields", objectMapper.valueToTree(pack.getResultFields()));
        root.set("qcRules", objectMapper.valueToTree(pack.getQcRules()));
        root.set("reportSections", objectMapper.valueToTree(pack.getReportSections()));
        root.set("evidenceRequirements", objectMapper.valueToTree(pack.getEvidenceRequirements()));
        if (StringUtils.hasText(overrideConfig)) {
            root.set("override", readObject(overrideConfig));
        }
        String json = root.toString();
        return new WorkflowSnapshot(json, sha256(json), frozenAt, pack.getDomainPackId(), pack.getPackCode(), pack.getPackVersion());
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

    private String sha256(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest((value == null ? "" : value).getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("SHA-256 algorithm is unavailable", ex);
        }
    }

}
