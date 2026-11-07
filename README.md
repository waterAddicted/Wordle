# Wordle

Joc Wordle in limba romana, facut pentru tema de Servlet.

- `backend/words-database` - servlet (WildFly) care tine lista de cuvinte
- `backend/game-service` - servlet (Tomcat) care tine sesiunile de joc si vorbeste cu `words-database`
- `frontend` - interfata in React
- `docs` - documentatia temei

## Rulare

```
docker compose up --build
```

- frontend: http://localhost:3000
- game-service: http://localhost:8081/gameservice

Detalii despre arhitectura si cum functioneaza totul sunt in [docs/documentatie.md](docs/documentatie.md).
