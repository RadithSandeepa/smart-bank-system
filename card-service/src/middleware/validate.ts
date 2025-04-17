/*
 *   Copyright (c) 2025 Radith Sandeepa
 *   All rights reserved.
 */
import { Request, Response, NextFunction } from "express";
import Joi from "joi";

export const validateBody = (schema: Joi.ObjectSchema) => {
  return (req: Request, res: Response, next: NextFunction) => {
    const { error } = schema.validate(req.body, { abortEarly: false });
    if (error) {
      res.status(400).json({
        message: "Validation failed",
        details: error.details.map((d) => d.message),
      });
      return;
    }
    next();
  };
};
