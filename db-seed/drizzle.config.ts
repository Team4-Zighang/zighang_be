import type { Config } from "drizzle-kit";
import { config } from "dotenv";
import * as path from "path";

config({ path: path.resolve(__dirname, "../../../secret/.env") });

export default {
  schema: "./drizzle/migrations/schema.ts",
  out: "./drizzle/migrations",
  dialect: "mysql",
  dbCredentials: {
    url: process.env.DATABASE_URL ?? "",
  },
} satisfies Config;