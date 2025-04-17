/*
 *   Copyright (c) 2025 Radith Sandeepa
 *   All rights reserved.
 */
import express from "express";
import {
  createCard,
  getCardById,
  updateCard,
  deleteCard,
  getCardsByAccount,
} from "../controllers/card.controller";

const router = express.Router();

// POST /api/cards → Create a new card
router.post("/", createCard);

// GET /api/cards/:id → Get card by ID
router.get("/:id", getCardById);

// PUT /api/cards/:id → Update card
router.put("/:id", updateCard);

// DELETE /api/cards/:id → Delete card
router.delete("/:id", deleteCard);

// GET /api/cards/account/:accountId → List all cards for an account
router.get("/account/:accountId", getCardsByAccount);

export default router;
