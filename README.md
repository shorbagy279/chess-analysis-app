# Chess Analysis Platform

AI-powered chess game analysis using Spring Boot, React, Stockfish, and Claude AI.

## 🎯 Features

- **Multi-Platform Support**: Analyze games from Lichess.org and Chess.com
- **Engine Analysis**: Stockfish-powered move-by-move analysis
- **AI Insights**: Claude AI provides personalized playing style analysis
- **Comprehensive Metrics**: Track blunders, mistakes, accuracy, and more
- **Opening Repertoire**: Analyze your most-played openings
- **Beautiful UI**: Modern, responsive React interface with charts

## 🏗️ Architecture

```
Backend (Spring Boot):
- REST API for analysis requests
- Integration with Lichess/Chess.com APIs
- Stockfish engine for position evaluation
- Claude API for AI insights
- PostgreSQL/H2 database

Frontend (React):
- Vite + React 18
- TailwindCSS for styling
- Recharts for data visualization
- Axios for API communication
```

## 📋 Prerequisites

- **Java 17** or higher
- **Node.js 18** or higher
- **Maven 3.8** or higher
- **Stockfish** chess engine ([Download](https://stockfishchess.org/download/))
- **Claude API Key** from Anthropic ([Get one](https://console.anthropic.com/))

## 🚀 Quick Start

### 1. Clone the Repository

```bash
git clone <your-repo-url>
cd chess-analysis-app
```

### 2. Backend Setup

#### Install Stockfish

**macOS:**
```bash
brew install stockfish
```

**Linux:**
```bash
sudo apt-get install stockfish
```

**Windows:**
Download from [stockfishchess.org](https://stockfishchess.org/download/) and add to PATH

#### Configure Application

Create `backend/src/main/resources/application-local.properties`:

```properties
# Claude API
claude.api.key=YOUR_CLAUDE_API_KEY_HERE

# Stockfish Path (update based on your installation)
stockfish.path=/usr/local/bin/stockfish

# Database (default uses H2 in-memory)
# For PostgreSQL, uncomment and configure:
# spring.datasource.url=jdbc:postgresql://localhost:5432/chessanalysis
# spring.datasource.username=postgres
# spring.datasource.password=yourpassword
```

#### Run Backend

```bash
cd backend
mvn clean install
mvn spring-boot:run -Dspring-boot.run.profiles=local
```

Backend will start on `http://localhost:8080`

### 3. Frontend Setup

```bash
cd frontend
npm install
npm run dev
```

Frontend will start on `http://localhost:3000`

## 🔑 Getting a Claude API Key

1. Go to [console.anthropic.com](https://console.anthropic.com/)
2. Sign up or log in
3. Navigate to API Keys section
4. Create a new API key
5. Add it to your `application-local.properties`

**Note:** Claude API is pay-as-you-go. Each analysis costs approximately $0.05-$0.10 depending on game count.

## 📖 Usage

1. Open `http://localhost:3000` in your browser
2. Select platform (Lichess or Chess.com)
3. Enter your username
4. Choose number of games to analyze (10-200)
5. Click "Analyze My Games"
6. Wait 1-3 minutes for analysis to complete
7. View your comprehensive analysis!

## 🎮 Example Usernames for Testing

**Lichess:**
- `DrNykterstein` (Magnus Carlsen)
- `Hikaru` (Hikaru Nakamura)
- `penguingm1` (Andrew Tang)

**Chess.com:**
- `magnuscarlsen`
- `hikaru`
- `fabianocaruana`

## 📊 What You Get

- **Playing Style Analysis**: AI-generated description of your chess personality
- **Skill Ratings**: Tactical, Positional, Endgame, Time Management ratings
- **Performance Metrics**: Win rate, accuracy, blunders, mistakes
- **Opening Repertoire**: Most-played openings with statistics
- **Personalized Recommendations**: AI-powered improvement suggestions
- **Common Mistake Patterns**: Identify recurring errors

## 🛠️ API Endpoints

```
POST   /api/analysis/start
GET    /api/analysis/user/{username}?platform=lichess
GET    /api/analysis/health
```

## 🗄️ Database Schema

The application uses 4 main tables:
- `users` - User accounts
- `games` - Chess games with PGN and metadata
- `analysis` - Stockfish analysis results
- `ai_insights` - Claude AI generated insights

## 🔧 Configuration Options

### Backend (`application.properties`)

```properties
# Analysis
analysis.default-game-count=50
analysis.max-game-count=200
analysis.timeout-seconds=300

# Stockfish
stockfish.depth=20
stockfish.threads=4

# Claude AI
claude.api.model=claude-sonnet-4-20250514
claude.api.max-tokens=4096
```

### Frontend

Create `frontend/.env.local`:

```
VITE_API_URL=http://localhost:8080/api
```

## 🐛 Troubleshooting

### Backend Issues

**Stockfish not found:**
```bash
# Find stockfish location
which stockfish

# Update application.properties with the path
stockfish.path=/path/to/stockfish
```

**Claude API errors:**
- Verify API key is correct
- Check you have credits in your Anthropic account
- Ensure you're using the correct model name

**Database errors:**
- H2 in-memory database resets on restart (this is normal for development)
- For persistent storage, configure PostgreSQL

### Frontend Issues

**CORS errors:**
- Ensure backend is running on port 8080
- Check CORS configuration in `CorsConfig.java`

**API connection failed:**
- Verify backend is running
- Check proxy configuration in `vite.config.js`

## 📈 Performance Tips

1. **Start with fewer games** (20-30) for faster results
2. **Stockfish depth**: Lower depth (10-15) for faster analysis, higher (20-25) for accuracy
3. **Caching**: Results are saved in database - subsequent requests for same user are instant
4. **API costs**: Monitor your Claude API usage at console.anthropic.com

## 🔮 Future Enhancements

- [ ] Opening recommendation system
- [ ] Compare with other players
- [ ] Progress tracking over time
- [ ] Puzzle generation from mistakes
- [ ] Mobile app version
- [ ] Export analysis as PDF
- [ ] Integration with chess.com Premium features

## 📝 License

MIT License - feel free to use this project for learning or commercial purposes

## 🤝 Contributing

Contributions welcome! Please feel free to submit a Pull Request.

## 📞 Support

For issues or questions:
1. Check the Troubleshooting section
2. Review existing GitHub issues
3. Create a new issue with details

## 🙏 Acknowledgments

- **Stockfish** - Open source chess engine
- **Anthropic Claude** - AI insights
- **Lichess.org** - Free chess platform with excellent API
- **Chess.com** - Popular chess platform

---

Made with ♟️ by chess enthusiasts, for chess enthusiasts
