/*
 *   Copyright (c) 2025 Radith Sandeepa
 *   All rights reserved.
 */
import express from "express";
import mongoose from "mongoose";

const router = express.Router();

router.get("/health", (req, res) => {
  const mongoStatus = mongoose.connection.readyState === 1 ? "connected" : "disconnected";

  res.status(200).json({
    status: "ok",
    service: "card-service",
    database: mongoStatus,
    timestamp: new Date().toISOString(),
  });
});

export default router;
