/*
 *   Copyright (c) 2025 Radith Sandeepa
 *   All rights reserved.
 */
import Joi from "joi";

export const cardSchema = Joi.object({
  accountId: Joi.string().required(),
  cardNumber: Joi.string().required(),
  cardType: Joi.string().valid("credit", "debit").required(),
  expiryDate: Joi.date().iso().required(),
  status: Joi.string().valid("active", "blocked").optional(),
});