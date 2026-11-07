const GAME_SERVICE_URL = import.meta.env.VITE_GAME_SERVICE_URL || "http://localhost:8081/gameservice";

export async function startGame() {
  const res = await fetch(`${GAME_SERVICE_URL}/api/game`, { method: "POST" });
  if (!res.ok) {
    throw new Error("Nu s-a putut porni jocul");
  }
  return res.json();
}

export async function submitGuess(sessionId, input) {
  const res = await fetch(`${GAME_SERVICE_URL}/api/guess`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ sessionId, input })
  });
  if (!res.ok) {
    const message = await res.text();
    throw new Error(message || "Incercare invalida");
  }
  return res.json();
}
