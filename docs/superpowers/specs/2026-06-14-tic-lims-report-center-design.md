# TIC LIMS Report Center Design

Date: 2026-06-14
Branch: `feature/tic-lims-report-center`

## 1. Purpose

The platform architecture treats reports as a first-class product capability, not as a loose text field at the end of LIMS execution. The current implementation already creates `ReportDraftPlan` from a frozen workflow snapshot, but the actual report center remains thin:

- `lab_template_version` stores only a preview JSON and field bindings.
- Template lifecycle does not protect published report templates from in-place edits.
- Report sections and output formats are not explicit persisted design assets.
- LIMS report issue does not create a canonical `EvidenceObject`.

This batch turns the existing template module into a minimum report designer and connects issued LIMS reports into the evidence-chain center.

## 2. Scope

In scope:

1. Extend report template versions with:
   - `template_status`: draft, published, archived.
   - `section_schema`: ordered report sections.
   - `output_formats`: JSON array such as `["WORD","PDF","EXCEL"]`.
   - `data_source_schema`: data source contract for report assembly.
2. Add template lifecycle service operations:
   - publish template.
   - archive template.
   - block update/delete when template is published or archived.
3. Make template preview return explicit sections, output formats, data sources, and field bindings.
4. Add frontend report template designer fields for lifecycle, sections, output formats, and data sources.
5. Add LIMS report evidence gateway:
   - when a report is issued, create a `REPORT` evidence object in LAB.
   - link it to report/CNAS clause context.
6. Add SQL bootstrap coverage and focused tests.

Out of scope:

- Real Word/PDF/Excel rendering engine.
- Rich drag-drop report page designer.
- Electronic signature and legally immutable file storage.
- AI-generated report narrative.

## 3. Domain Boundaries

### 3.1 LAB Report Template Context

LAB owns report templates because they are compliance and method/domain assets shared across LIMS execution.

Entities:

- `LabTemplateVersion`
- `LabTemplateFieldBinding`

This batch extends `LabTemplateVersion` but does not add new tables. The explicit schema fields are enough for a minimum designer:

```text
LabTemplateVersion
  - templateCode
  - templateVersion
  - templateStatus
  - templateType
  - sectionSchema
  - outputFormats
  - dataSourceSchema
  - previewSchema
```

### 3.2 LIMS Report Execution Context

LIMS owns report generation and issuing. It may create report evidence, but must not read LAB evidence mappers directly.

Boundary rule:

```text
LIMS Report Issue
  -> ReportEvidenceGateway
  -> LAB EvidenceObject / EvidenceLink services
```

No direct `yudao-module-lab.dal.mysql.*` imports inside `yudao-module-lims`.

## 4. Template Lifecycle

Allowed transitions:

```text
draft -> published -> archived
```

Rules:

- draft templates are editable.
- published templates cannot be edited or deleted in place.
- archived templates cannot be edited or deleted in place.
- published templates can be archived.
- a future copy-version action can produce a new draft, but that is not required in this batch.

## 5. Report Issue Evidence Loop

When `/lims/report/issue` is called:

1. Load report and request.
2. Ensure report has report number, request number, and data snapshot.
3. Mark report as `issued`.
4. Create evidence object:
   - type: `REPORT`
   - source object: `lims_report`
   - source id: report id
   - source no: report no
   - business domain: `report`
   - file url: report file url when present
   - hash: from data snapshot hash or generated stable metadata hash
5. Create evidence link:
   - linked business type: `lims_request`
   - linked business id: request id
   - linked business no: request no
   - clause category: `report`
   - clause id: first matching report clause when available

This closes the report side of the evidence chain:

```text
LIMS Request
  -> LIMS Report
  -> EvidenceObject(REPORT)
  -> EvidenceLink(report clause)
```

## 6. Acceptance Criteria

1. Template version supports explicit sections, output formats, data source schema, and lifecycle status.
2. Published/archived templates cannot be edited or deleted.
3. Template preview includes sections, output formats, data sources, and field bindings.
4. Admin UI exposes report template designer controls for lifecycle, sections, formats, and data sources.
5. Issuing a LIMS report creates a LAB report evidence object and evidence link through a LIMS gateway.
6. Unit tests cover template lifecycle and report issue evidence.
7. Maven target tests and server compile pass.
8. Frontend build passes.
9. Browser screenshot verifies report template designer.
10. Architecture grep confirms LIMS still does not import LAB mappers.

## 7. Risks

- Existing schemas need incremental migrations for new columns because the bootstrap SQL is create-if-not-exists oriented.
- This batch stores report design schemas as JSON text. A visual drag-drop designer can later write these schemas but does not need its own persistence boundary now.
- Report file rendering remains a separate batch; `outputFormats` declares expected outputs and supports later renderers.
