# Case Study Scenarios to discuss

## Scenario 1: Cost Allocation and Tracking
**Situation**: The company needs to track and allocate costs accurately across different Warehouses and Stores. The costs include labor, inventory, transportation, and overhead expenses.

**Task**: Discuss the challenges in accurately tracking and allocating costs in a fulfillment environment. Think about what are important considerations for this, what are previous experiences that you have you could related to this problem and elaborate some questions and considerations

**Questions you may have and considerations:**
- **Key points:** Define cost categories (labor, inventory carrying, transport, overhead); choose allocation drivers (hours, inventory value, shipments); ensure traceability to warehouse/store/SKU.
- **Challenges:** Shared-resource allocation, timeliness vs accuracy, auditability.
- **Next steps:** Inventory data sources (WMS/OMS/TMS), required reporting cadence, and chart-of-accounts mapping.

## Scenario 2: Cost Optimization Strategies
**Situation**: The company wants to identify and implement cost optimization strategies for its fulfillment operations. The goal is to reduce overall costs without compromising service quality.

**Task**: Discuss potential cost optimization strategies for fulfillment operations and expected outcomes from that. How would you identify, prioritize and implement these strategies?

**Questions you may have and considerations:**
- **Key points:** Focus on high-impact levers: routing/transport, inventory rebalancing, labor optimization, slotting and SKU rationalization.
- **How to act:** Use Pareto analysis to prioritize, pilot changes, measure ROI, then scale.
- **Risks:** Watch SLAs and capacity constraints when relocating inventory.

## Scenario 3: Integration with Financial Systems
**Situation**: The Cost Control Tool needs to integrate with existing financial systems to ensure accurate and timely cost data. The integration should support real-time data synchronization and reporting.

**Task**: Discuss the importance of integrating the Cost Control Tool with financial systems. What benefits the company would have from that and how would you ensure seamless integration and data synchronization?

**Questions you may have and considerations:**
- **Key points:** Integrate via idempotent, auditable interfaces (API or batched journals) and map operational dimensions to GL codes.
- **Requirements:** Idempotence, retries, reconciliation reports, and clear GL mapping.
- **Questions:** Which ERP and integration patterns are supported (API, SFTP, message bus)?

## Scenario 4: Budgeting and Forecasting
**Situation**: The company needs to develop budgeting and forecasting capabilities for its fulfillment operations. The goal is to predict future costs and allocate resources effectively.

**Task**: Discuss the importance of budgeting and forecasting in fulfillment operations and what would you take into account designing a system to support accurate budgeting and forecasting?

**Questions you may have and considerations:**
- **Key points:** Forecast by drivers (orders, seasonality, promotions) not simple history; include lead times and supplier risk.
- **Design:** Scenario modeling (best/worst/base), variance feedback loop, and driver-level dashboards.
- **Questions:** Forecast horizon and acceptable error margins?

## Scenario 5: Cost Control in Warehouse Replacement
**Situation**: The company is planning to replace an existing Warehouse with a new one. The new Warehouse will reuse the Business Unit Code of the old Warehouse. The old Warehouse will be archived, but its cost history must be preserved.

**Task**: Discuss the cost control aspects of replacing a Warehouse. Why is it important to preserve cost history and how this relates to keeping the new Warehouse operation within budget?

**Questions you may have and considerations:**
- **Key points:** Archive old warehouse record (preserve PK) and create a new entity that can reuse the BU code for lookups but not for historical ledger entries.
- **Controls:** Record migration transactions (transfer costs), preserve audit trail, and keep historical costs tied to archived PK.
- **Questions:** Do external systems reference BU code (integration impact) and what retention policies apply?

## Instructions for Candidates
Before starting the case study, read the [BRIEFING.md](BRIEFING.md) to quickly understand the domain, entities, business rules, and other relevant details.

**Analyze the Scenarios**: Carefully analyze each scenario and consider the tasks provided. To make informed decisions about the project's scope and ensure valuable outcomes, what key information would you seek to gather before defining the boundaries of the work? Your goal is to bridge technical aspects with business value, bringing a high level discussion; no need to deep dive.
