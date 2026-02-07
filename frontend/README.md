# Toy Commerce Frontend

React frontend for the Toy Commerce backend.

## Stack

- Vite + React + TypeScript
- React Router
- TanStack Query
- Tailwind CSS

## Run

```bash
npm install
npm run dev
```

Default API endpoint:

- `http://localhost:8080` (set via `VITE_API_BASE_URL`)

## API contract sync

Generate API client artifacts from the backend OpenAPI endpoint:

```bash
OPENAPI_SOURCE=http://localhost:8080/v3/api-docs npm run generate:api
```

## Key routes

- `/` products
- `/products/:productId` product detail
- `/wishlist/ranking` wishlist ranking
- `/cart` cart
- `/orders/:orderId` order detail and payment
- `/admin/products` admin center
