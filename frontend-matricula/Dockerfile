# Etapa 1: Compilar Angular
FROM node:18-alpine AS builder
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
# Construimos la version de produccion
RUN npm run build -- --configuration production

# Etapa 2: Servir con Nginx
FROM nginx:alpine
# Borramos la pagina por defecto de nginx
RUN rm -rf /usr/share/nginx/html/*
# Copiamos nuestra configuracion de nginx
COPY nginx.conf /etc/nginx/conf.d/default.conf
# Copiamos la app compilada de Angular. 
# Nota: Angular 17 suele meter los archivos en una subcarpeta 'browser'. 
# Si falla, podemos ajustar esta linea.
COPY --from=builder /app/dist/frontend-matricula /usr/share/nginx/html

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
