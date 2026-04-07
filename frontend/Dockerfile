# ═══════════════════════════════════════════════════════════════
#  Stage 1 — Build the React app with Vite
# ═══════════════════════════════════════════════════════════════
FROM node:20-alpine AS builder

WORKDIR /app

# Install dependencies first (cached until package.json changes)
COPY package.json package-lock.json ./
RUN npm ci

# Build production bundle
COPY . .
RUN npm run build

# ═══════════════════════════════════════════════════════════════
#  Stage 2 — Serve static files with Nginx
# ═══════════════════════════════════════════════════════════════
FROM nginx:alpine

# Static build output
COPY --from=builder /app/dist /usr/share/nginx/html

# Custom Nginx config (SPA routing + /api proxy)
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80
