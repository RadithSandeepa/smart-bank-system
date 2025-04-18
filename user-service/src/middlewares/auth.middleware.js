import jwt from "jsonwebtoken";

export const authenticate = (req, res, next) => {
  const token = req.headers.authorization?.split(" ")[1];
  if (!token) return res.status(401).json({ error: "Unauthorized" });

  try {
    const decoded = jwt.verify(token, process.env.JWT_SECRET);
    req.user = decoded;
    next();
  } catch (error) {
    res.status(401).json({ error: "Invalid token" });
  }
};

export const authorizeAdmin = (req, res, next) => {
  if (req.user.role !== "admin") {
    return res.status(403).json({ error: "Access denied. Admins only." });
  }
  next();
};

export const authorizeAdminOrCurrentUser = async (req, res, next) => {
  const { id } = req.params;

  if (req.user.role === "admin" || req.user.id === id) {
    return next();
  }

  return res
    .status(403)
    .json({ error: "Access denied. Admins or the current user only." });
};
