# TIC LIMS Report Template Designer Product Design

Date: 2026-06-15
Scope: Word / PDF / Excel report-template design capability for the TIC LIMS platform.

## Product Positioning

Report templates are a standalone product capability, not a field on the report list.

The module should let a laboratory configure how a report is assembled for different industries and customers, while keeping the template version frozen once published. It should support third-party TIC delivery and internal lab reporting with the same abstraction.

## Current Baseline

Existing implementation already has:

- Template version table: `lab_template_version`.
- Field binding table: `lab_template_field_binding`.
- Lifecycle: `draft -> published -> archived`.
- Output format schema: `WORD`, `PDF`, `EXCEL`.
- Report section schema.
- Data source schema.
- Preview API: `/lab/template/preview`.

Current product gap:

- The page still reads like a generic CRUD configuration page.
- Word / PDF / Excel are selected as checkboxes, but users cannot reason about format-specific output behavior.
- Preview shows raw JSON instead of a report designer mental model.
- Field binding exists, but is separated from sections, data sources, and output channels.

## User Jobs

1. Lab quality or reporting manager creates a report template for a domain pack.
2. Template designer configures report sections, output formats, and data bindings.
3. Operator previews whether the report can be generated for Word / PDF / Excel.
4. Authorized user publishes the template version.
5. LIMS report generation consumes the published immutable template version.

## Domain Model

ReportTemplateVersion:

- Identity: templateCode + templateVersion.
- Scope: domainPackId.
- Lifecycle: draft, published, archived.
- Output channels: Word, PDF, Excel.
- Section schema: ordered report chapters.
- Data source schema: business source paths.
- Field bindings: report field to source path.
- Preview schema: layout and rendering profile metadata.

ReportTemplateDesigner should expose the domain model in four product areas:

- Template lifecycle and version governance.
- Output channel design.
- Section and layout design.
- Data binding and preview validation.

## IA And Interaction Design

### Page Header

Use a product-level header:

- Title: `报告模板设计器`.
- Subtitle: describes Word / PDF / Excel template design.
- Action: create template.
- Summary cards: total templates, published versions, output channels, field bindings.

### Template List

Keep the table because labs need searchable version management.

Improve the table role:

- Show lifecycle status.
- Show output format chips.
- Keep actions: fields, preview, edit, publish, archive, delete.

### Design Workbench

When a template is selected, show a workbench below the table:

- Left/top: selected template metadata.
- Center: report canvas preview, generated from ordered sections.
- Right/bottom: field bindings and data sources.
- Output tabs: Word, PDF, Excel.

MVP preview does not need to render true `.docx`, `.pdf`, `.xlsx`; it should show each channel's intended structure and whether the template has enough schema to generate the file. Real binary rendering remains in the report generator.

### Template Editor Dialog

Keep a modal for first implementation, but make it a designer surface:

- Basic metadata.
- Output channels.
- Report section table.
- Data source table.
- Preview schema, with a sensible default.

Later this can become a full-page designer without changing backend contracts.

## MVP Implementation Plan

Phase 1, current implementation target:

- Rename the page experience from `模板编制` style to `报告模板设计器`.
- Add report-template dashboard cards.
- Add a selected-template workbench.
- Add output-channel tabs for Word / PDF / Excel.
- Render a report canvas from `sectionSchema`.
- Show data-source and field-binding panels next to the canvas.
- Replace raw preview dialog with structured sections / fields / schema tabs.
- Keep the existing backend API and table schema unchanged.

Phase 2:

- Add format-specific profile editors in `previewSchema`, for example page size, header/footer, watermark, signature block, Excel sheet mapping.
- Add template duplication: published version -> new draft version.
- Add validation: missing required fields, missing result table, missing signature section.

Phase 3:

- Integrate a true binary template engine:
  - Word: DOCX placeholder and section renderer.
  - PDF: HTML/PDF renderer with compliance footer and signature block.
  - Excel: sheet templates for result tables and raw result exports.
- Add template import/export and customer-specific template inheritance.

## Design Constraints

- Stay inside Ruoyi / Element Plus admin style.
- Do not add new frontend dependencies.
- Do not bypass template version immutability.
- Do not put report design logic inside LIMS report list.
- Keep domain-pack linkage: templates belong to direction packages and published versions are consumed by execution/report plans.

## Acceptance Criteria

- Users can understand Word / PDF / Excel output capability from the first screen.
- Users can select a template and see a report-canvas style preview without reading raw JSON.
- Users can see sections, data sources, and field bindings together.
- Existing create/update/publish/archive/delete behavior still works.
- Frontend build passes.
- Authenticated browser screenshots show the designer page without layout overflow on desktop and mobile.
