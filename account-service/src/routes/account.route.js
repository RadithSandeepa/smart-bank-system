/*
 *   Copyright (c) 2025 Radith Sandeepa
 *   All rights reserved.
 */
import express from "express";
import {
  createAccount,
  getAccountById,
  updateAccount,
  deleteAccount,
  getAccountsByUser,
} from "../controllers/account.controller.js";

const router = express.Router();

// Create a new account
router.post("/", createAccount);

// Get account by ID
router.get("/:id", getAccountById);

// Update an account by ID
router.put("/:id", updateAccount);

// Delete an account by ID
router.delete("/:id", deleteAccount);

// Get all accounts for a user by userId
router.get("/user/:userId", getAccountsByUser);

export default router;
