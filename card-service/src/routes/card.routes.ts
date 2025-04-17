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
import { validateBody } from "../middleware/validate";
import { cardSchema } from "../validations/card.validation";

const router = express.Router();

/**
 * @swagger
 * components:
 *   schemas:
 *     Card:
 *       type: object
 *       required:
 *         - accountId
 *         - cardNumber
 *         - cardType
 *         - expiryDate
 *       properties:
 *         accountId:
 *           type: string
 *         cardNumber:
 *           type: string
 *         cardType:
 *           type: string
 *           enum: [credit, debit]
 *         expiryDate:
 *           type: string
 *           format: date
 *         status:
 *           type: string
 *           enum: [active, blocked]
 */

/**
 * @swagger
 * /cards:
 *   post:
 *     summary: Create a new card
 *     tags: [Card]
 *     requestBody:
 *       required: true
 *       content:
 *         application/json:
 *           schema:
 *             $ref: '#/components/schemas/Card'
 *     responses:
 *       201:
 *         description: Card created successfully
 *       400:
 *         description: Validation error
 */
// POST /api/cards → Create a new card
router.post("/", validateBody(cardSchema), createCard);

/**
 * @swagger
 * /cards/{id}:
 *   get:
 *     summary: Get a card by ID
 *     tags: [Card]
 *     parameters:
 *       - in: path
 *         name: id
 *         required: true
 *         schema:
 *           type: string
 *         description: The card ID
 *     responses:
 *       200:
 *         description: Card retrieved successfully
 *         content:
 *           application/json:
 *             schema:
 *               $ref: '#/components/schemas/Card'
 *       404:
 *         description: Card not found
 */
// GET /api/cards/:id → Get card by ID
router.get("/:id", getCardById);

/**
 * @swagger
 * /cards/{id}:
 *   put:
 *     summary: Update a card by ID
 *     tags: [Card]
 *     parameters:
 *       - in: path
 *         name: id
 *         required: true
 *         schema:
 *           type: string
 *         description: The card ID
 *     requestBody:
 *       required: true
 *       content:
 *         application/json:
 *           schema:
 *             $ref: '#/components/schemas/Card'
 *     responses:
 *       200:
 *         description: Card updated successfully
 *       400:
 *         description: Invalid data or update error
 *       404:
 *         description: Card not found
 */
// PUT /api/cards/:id → Update card
router.put("/:id", validateBody(cardSchema), updateCard);

/**
 * @swagger
 * /cards/{id}:
 *   delete:
 *     summary: Delete a card by ID
 *     tags: [Card]
 *     parameters:
 *       - in: path
 *         name: id
 *         required: true
 *         schema:
 *           type: string
 *         description: The card ID
 *     responses:
 *       200:
 *         description: Card deleted successfully
 *       404:
 *         description: Card not found
 */
// DELETE /api/cards/:id → Delete card
router.delete("/:id", deleteCard);

/**
 * @swagger
 * /cards/account/{accountId}:
 *   get:
 *     summary: Get all cards for an account
 *     tags: [Card]
 *     parameters:
 *       - in: path
 *         name: accountId
 *         required: true
 *         schema:
 *           type: string
 *         description: The account ID
 *     responses:
 *       200:
 *         description: List of cards
 *         content:
 *           application/json:
 *             schema:
 *               type: array
 *               items:
 *                 $ref: '#/components/schemas/Card'
 *       404:
 *         description: No cards found for the account
 */
// GET /api/cards/account/:accountId → List all cards for an account
router.get("/account/:accountId", getCardsByAccount);

export default router;
