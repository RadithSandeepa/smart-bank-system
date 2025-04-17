import { Router } from "express";
import { 
  createTransaction, 
  getTransactions, 
  getTransactionById,
  getTransactionStats 
} from "../controllers/transaction.controller.js";

const router = Router();


/**
 * @route POST /transactions
 * @desc Create a new transaction
 * @access Public
 */
router.post("/", createTransaction);

/**
 * @route GET /transactions
 * @desc Get all transactions with optional filters
 * @access Public
 */
router.get("/", getTransactions);

/**
 * @route GET /transactions/stats
 * @desc Get transaction statistics
 * @access Public
 */
router.get("/stats", getTransactionStats);

/**
 * @route GET /transactions/:transaction_id
 * @desc Get a single transaction by ID
 * @access Public
 */
router.get("/:transaction_id", getTransactionById);

export default router;