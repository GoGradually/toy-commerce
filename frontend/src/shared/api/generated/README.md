This directory stores API contract artifacts.

- `schema.ts`: current snapshot used by the app.
- `client/`: generated output from `npm run generate:api`.

Regenerate from live backend:

```bash
OPENAPI_SOURCE=http://localhost:8080/v3/api-docs npm run generate:api
```
