import express from "express";
import {
  registerUser,
  loginUser,
  getUsers,
  updateUser,
  deleteUser,
  getUserById,
} from "../controllers/user.controller.js";
import {
  authenticate,
  authorizeAdmin,
  authorizeAdminOrCurrentUser,
} from "../middlewares/auth.middleware.js";

const router = express.Router();

router.post("/register", registerUser);
router.post("/login", loginUser);
router.get("/", authenticate, authorizeAdmin, getUsers); // Only admins can get all users
router.get("/:id", authenticate, authorizeAdminOrCurrentUser, getUserById); // Admins or current user can get a single user
router.put("/:id", authenticate, authorizeAdminOrCurrentUser, updateUser); // Admins or current user can update
router.delete("/:id", authenticate, authorizeAdmin, deleteUser); // Only admins can delete

export default router;
