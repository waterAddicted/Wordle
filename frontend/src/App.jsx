import { useCallback, useEffect, useState } from "react";
import { startGame, submitGuess } from "./api.js";

const WORD_LENGTH = 5;
const KEYBOARD_ROWS = [
  ["Q", "W", "E", "R", "T", "Y", "U", "I", "O", "P"],
  ["A", "S", "D", "F", "G", "H", "J", "K", "L"],
  ["ENTER", "Z", "X", "C", "V", "B", "N", "M", "DEL"],
];

export default function App() {
  const [session, setSession] = useState(null);
  const [rows, setRows] = useState([]);
  const [current, setCurrent] = useState("");
  const [status, setStatus] = useState("IN_PROGRESS");
  const [revealedWord, setRevealedWord] = useState(null);
  const [message, setMessage] = useState("");
  const [loading, setLoading] = useState(false);
  const [letterStates, setLetterStates] = useState({});

  const newGame = useCallback(async () => {
    setLoading(true);
    setMessage("");
    try {
      const data = await startGame();
      setSession(data);
      setRows([]);
      setCurrent("");
      setStatus("IN_PROGRESS");
      setRevealedWord(null);
      setLetterStates({});
    } catch (err) {
      setMessage(err.message);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    newGame();
  }, [newGame]);

  const mergeLetterStates = (letters, results) => {
    setLetterStates((prev) => {
      const next = { ...prev };
      letters.forEach((letter, i) => {
        const result = results[i];
        const rank = { wrong: 0, misplaced: 1, correct: 2 };
        if (!next[letter] || rank[result] > rank[next[letter]]) {
          next[letter] = result;
        }
      });
      return next;
    });
  };

  const handleSubmit = useCallback(async () => {
    if (status !== "IN_PROGRESS" || loading) return;
    if (current.length !== WORD_LENGTH) {
      setMessage(`Cuvantul trebuie sa aiba ${WORD_LENGTH} litere`);
      return;
    }
    setLoading(true);
    setMessage("");
    try {
      const data = await submitGuess(session.sessionId, current);
      const letters = data.input;
      const results = data.response;
      setRows((prev) => [...prev, { letters, results }]);
      mergeLetterStates(letters, results);
      setCurrent("");
      setStatus(data.status);
      if (data.word) setRevealedWord(data.word);
    } catch (err) {
      setMessage(err.message);
    } finally {
      setLoading(false);
    }
  }, [current, session, status, loading]);

  const handleKey = useCallback(
    (key) => {
      if (status !== "IN_PROGRESS" || loading) return;
      if (key === "ENTER") {
        handleSubmit();
      } else if (key === "DEL") {
        setCurrent((c) => c.slice(0, -1));
      } else if (/^[A-Z]$/.test(key) && current.length < WORD_LENGTH) {
        setCurrent((c) => c + key);
      }
    },
    [current, status, loading, handleSubmit],
  );

  useEffect(() => {
    const onKeyDown = (e) => {
      const key = e.key.toUpperCase();
      if (key === "ENTER") handleKey("ENTER");
      else if (key === "BACKSPACE") handleKey("DEL");
      else if (/^[A-Z]$/.test(key)) handleKey(key);
    };
    window.addEventListener("keydown", onKeyDown);
    return () => window.removeEventListener("keydown", onKeyDown);
  }, [handleKey]);

  const attemptsRemaining = session ? session.maxAttempts - rows.length : 0;

  return (
    <div className="app">
      <h1>Wordle</h1>

      {status !== "IN_PROGRESS" && (
        <div className="banner">
          {status === "WON"
            ? "Felicitari, ai ghicit!"
            : `Ai pierdut. Cuvantul era ${revealedWord}.`}
          <button onClick={newGame}>Joc nou</button>
        </div>
      )}

      {message && <div className="message">{message}</div>}

      <Board
        rows={rows}
        current={current}
        wordLength={WORD_LENGTH}
        maxAttempts={session?.maxAttempts || 6}
      />

      <Keyboard
        rows={KEYBOARD_ROWS}
        letterStates={letterStates}
        onKey={handleKey}
      />

      {session && status === "IN_PROGRESS" && (
        <div className="attempts">Incercari ramase: {attemptsRemaining}</div>
      )}
    </div>
  );
}

function Board({ rows, current, wordLength, maxAttempts }) {
  const displayRows = [...rows];
  if (displayRows.length < maxAttempts) {
    displayRows.push({ letters: current.split(""), results: [] });
  }
  while (displayRows.length < maxAttempts) {
    displayRows.push({ letters: [], results: [] });
  }

  return (
    <div className="board">
      {displayRows.map((row, i) => (
        <div className="row" key={i}>
          {Array.from({ length: wordLength }).map((_, j) => {
            const letter = row.letters[j] || "";
            const result = row.results[j];
            return (
              <div className={`tile ${result || ""}`} key={j}>
                {letter}
              </div>
            );
          })}
        </div>
      ))}
    </div>
  );
}

function Keyboard({ rows, letterStates, onKey }) {
  return (
    <div className="keyboard">
      {rows.map((row, i) => (
        <div className="keyboard-row" key={i}>
          {row.map((key) => (
            <button
              key={key}
              className={`key ${letterStates[key] || ""} ${key.length > 1 ? "wide" : ""}`}
              onClick={() => onKey(key)}
            >
              {key === "DEL" ? "⌫" : key}
            </button>
          ))}
        </div>
      ))}
    </div>
  );
}
