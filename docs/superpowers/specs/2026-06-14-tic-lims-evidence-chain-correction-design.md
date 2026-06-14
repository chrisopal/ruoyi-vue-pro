# TIC LIMS Evidence Chain Correction Design

Date: 2026-06-14
Branch: `feature/tic-lims-evidence-chain`

## 1. Why This Correction Exists

The first implementation batch correctly froze domain-pack snapshots, derived execution plans, bound equipment to LIMS tasks, and carried calibration evidence snapshots into reports. However, the original architecture also required an Evidence Chain Center where reports, raw records, equipment certificates, personnel authorizations, environment records, method validations, and audit records are first-class evidence objects that can be mapped to CNAS/CMA clauses.

The current implementation still stores most evidence as `lab_evidence_link` rows. That is useful for associations, but it is not enough for the product architecture:

- A link is not a reusable evidence asset.
- A report, device certificate, or raw record needs its own stable identity, hash, source, status, and optional file metadata.
- CNAS/CMA clause mapping should point to an evidence object when possible, not duplicate file metadata on every link.
- Equipment calibration evidence should create or refresh an evidence object so the equipment evidence-chain loop is auditable outside a single LIMS task/report.

This batch corrects that drift without broadening into the full report designer or AI interpretation center.

## 2. Scope

In scope:

1. Add `lab_evidence_object` as the canonical evidence asset table.
2. Add LAB backend CRUD/query service and admin controller for evidence objects.
3. Extend evidence links with `evidence_object_id`, clause id, capability scope id, link reason, verified by, and verified at.
4. Make equipment calibration/traceability creation create an evidence object and link it to the equipment/CNAS clause context.
5. Add frontend evidence object management page.
6. Add SQL menu seed and first bootstrap seed objects for representative evidence types.
7. Add focused unit tests and browser verification.

Out of scope for this batch:

- Full report template designer and Word/PDF/Excel rendering.
- AI standards Q&A, report interpretation, or experiment-data interpretation.
- Full CNAS readiness scoring dashboard.
- Immutable evidence storage or external file-object repository integration.

## 3. Domain Model

### 3.1 EvidenceObject

`EvidenceObject` is the canonical record for a piece of evidence.

Required fields:

- `evidence_code`: stable business code, such as `EQ-CERT-100-2026`.
- `evidence_name`: readable name.
- `evidence_type`: report, raw data, equipment certificate, personnel authorization, environment record, method validation, audit record, etc.
- `source_object`, `source_object_id`, `source_object_no`: source aggregate and business number.
- `business_domain`: equipment, report, sample, method, personnel, environment, audit, etc.
- `status`: draft, effective, expired, revoked.
- `evidence_hash`: SHA-256 hash, generated from stable metadata if not supplied.

Optional fields:

- `file_url`, `file_name`, `file_format`, `issued_by`, `issued_at`, `valid_from`, `valid_to`, `summary`.

### 3.2 EvidenceLink

`EvidenceLink` remains the mapping table, but after this correction it should map evidence objects to clauses/business targets.

Additional fields:

- `evidence_object_id`
- `clause_id`
- `capability_scope_id`
- `link_reason`
- `verified_by`
- `verified_at`

Compatibility rule:

- Existing API fields stay compatible.
- When `evidenceObjectId` is present, evidence code/name/url/hash should be filled from the evidence object unless explicitly supplied for backward compatibility.

## 4. Equipment Calibration Evidence Loop

When a calibration or traceability record is created:

1. Validate equipment asset exists.
2. Normalize the traceability type and status.
3. Insert the traceability record.
4. Upsert or create an `EvidenceObject`:
   - type: `EQUIPMENT_CERTIFICATE`
   - source object: `lab_equipment_traceability`
   - source id: traceability id
   - source no: certificate no
   - domain: `equipment`
   - status: `effective` when traceability is valid
5. Create an `EvidenceLink` from the evidence object back to:
   - linked business type: `equipment_asset`
   - linked business id: equipment id
   - clause category: `equipment`
   - clause id: best matching CNAS equipment clause when available

This gives the product a real equipment evidence-chain loop:

```text
EquipmentAsset
  -> EquipmentTraceability / CalibrationCertificate
  -> EvidenceObject
  -> EvidenceLink
  -> CNAS equipment clause / capability scope
```

## 5. DDD Boundary Rules

- Evidence object and evidence link belong to `yudao-module-lab`, because they are compliance/evidence-chain domain assets.
- `yudao-module-lims` must not read LAB evidence mappers directly.
- Future LIMS/report use of evidence objects should go through a LAB-facing gateway/query service.
- This batch may enhance LAB internals and frontend pages; it must not pull LAB persistence into LIMS.

## 6. Acceptance Criteria

1. `lab_evidence_object` exists in SQL with indexes for source, type/status, and code.
2. Evidence object CRUD is available under `/lab/evidence-object`.
3. Evidence link can store `evidence_object_id`, `clause_id`, capability scope, link reason, and verification metadata.
4. Creating an equipment calibration record creates an evidence object and a link.
5. Evidence object management page renders in the admin UI.
6. Targeted unit tests pass.
7. `yudao-server` compile passes.
8. Frontend build passes.
9. Browser screenshot verifies the evidence object page.
10. Architecture grep confirms LIMS still does not directly import LAB mappers.

## 7. Risks

- Existing deployed schemas need incremental ALTER statements if they already created `lab_evidence_link`; this repository's bootstrap script is additive and create-if-not-exists oriented.
- Full legal/audit-grade immutable evidence storage is deferred; this batch creates stable metadata and hash fields only.
- Clause selection for equipment starts with best-effort matching by `clause_category = equipment` or `clause_code = 6.4`; a richer requirement engine belongs in the CNAS readiness batch.
