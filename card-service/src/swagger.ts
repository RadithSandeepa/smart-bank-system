/*
 *   Copyright (c) 2025 Radith Sandeepa
 *   All rights reserved.
 */
import swaggerJSDoc from "swagger-jsdoc";
import swaggerUi from "swagger-ui-express";
import { Express } from "express";

const options: swaggerJSDoc.Options = {
  definition: {
    openapi: "3.0.0",
    info: {
      title: "Card Management API",
      version: "1.0.0",
      description: "API for managing credit/debit cards",
    },
    servers: [
      {
        url: "http://localhost:4001/api",
      },
    ],
  },
  apis: ["./src/routes/*.ts"],
};

const swaggerSpec = swaggerJSDoc(options);

export const setupSwagger = (app: Express) => {
  app.use("/api-docs", swaggerUi.serve, swaggerUi.setup(swaggerSpec));
};
