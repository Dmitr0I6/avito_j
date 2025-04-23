# bulletinboard/frontend/Dockerfile
FROM node:18-alpine

WORKDIR /app

COPY package.json package-lock.json ./
RUN npm install

COPY . .

ENV NODE_ENV production
RUN npm run build

RUN npm install -g serve
CMD ["serve", "-s", "build", "-l", "3000"]