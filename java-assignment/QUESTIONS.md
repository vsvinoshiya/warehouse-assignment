# Questions

Here we have 3 questions related to the code base for you to answer. It is not about right or wrong, but more about what's the reasoning behind your decisions.

1. In this code base, we have some different implementation strategies when it comes to database access layer and manipulation. If you would maintain this code base, would you refactor any of those? Why?

**Answer:**
```txt
I would refactor the persistence layer toward a more consistent pattern so the domain uses a single abstraction for data access. The warehouse code currently uses a repository-style adapter, while the store and product code are closer to Panache entity usage, so bringing those under one consistent model would reduce duplication, make transaction behavior easier to reason about, and simplify future maintenance.
```
----
2. When it comes to API spec and endpoints handlers, we have an Open API yaml file for the `Warehouse` API from which we generate code, but for the other endpoints - `Product` and `Store` - we just coded directly everything. What would be your thoughts about what are the pros and cons of each approach and what would be your choice?

**Answer:**
```txt
Using OpenAPI-generated interfaces is great for keeping the contract and implementation aligned, especially for shared APIs and less boilerplate. The downside is that generated code can feel less flexible when the implementation needs custom behavior or domain-specific validation. I would use generated code for the public contract where possible, but keep a thin hand-written adapter layer to add business rules and edge-case handling.
```
----
3. Given the need to balance thorough testing with time and resource constraints, how would you prioritize and implement tests for this project? Which types of tests would you focus on, and how would you ensure test coverage remains effective over time?

**Answer:**
```txt
I would prioritize unit tests around the use cases and validation rules first, since those encode most of the business logic and are fast to run. I would add a smaller set of integration tests for the REST endpoints and persistence flow to confirm that the layers work together. Over time, I would keep the suite focused on critical workflows, and add regression tests whenever a bug is fixed or a business rule changes.
```