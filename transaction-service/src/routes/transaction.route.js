/*
 *   Copyright (c) 2025 Radith Sandeepa
 *   All rights reserved.
 */
import express from "express";
import {
  createTransaction,
  getTransactionById,
  updateTransaction,
  deleteTransaction,
  getTransactionsByAccount,
} from "../controllers/transaction.controller.js";

const router = express.Router();

// Create a new transaction
router.post("/", createTransaction);

// Get a transaction by ID
router.get("/:id", getTransactionById);

// Update a transaction by ID
router.put("/:id", updateTransaction);

// Delete a transaction by ID
router.delete("/:id", deleteTransaction);

// Get all transactions for an account
router.get("/account/:accountId", getTransactionsByAccount);

export default router;
