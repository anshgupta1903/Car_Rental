import axios from "axios";

export async function bookRide(payload) {
  const res = await axios.post("/api/forms/book", payload);
  return res.data;
}
