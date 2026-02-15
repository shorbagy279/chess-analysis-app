import { useState } from 'react'
import { analysisAPI } from '../services/api'

function UsernameForm({ onAnalysisStart, onAnalysisComplete, onError }) {
  const [username, setUsername] = useState('')
  const [platform, setPlatform] = useState('lichess')
  const [gameCount, setGameCount] = useState(50)

  const handleSubmit = async (e) => {
    e.preventDefault()

    if (!username.trim()) {
      onError('Please enter a username')
      return
    }

    onAnalysisStart()

    try {
      const result = await analysisAPI.startAnalysis(username, platform, gameCount)
      onAnalysisComplete(result)
    } catch (error) {
      onError(error.message)
    }
  }

  return (
    <div className="max-w-2xl mx-auto">
      <div className="bg-white rounded-xl shadow-lg p-8">
        <div className="text-center mb-8">
          <h2 className="text-2xl font-bold text-gray-900 mb-2">
            Analyze Your Chess Games
          </h2>
          <p className="text-gray-600">
            Get AI-powered insights about your playing style, strengths, and areas for improvement
          </p>
        </div>

        <form onSubmit={handleSubmit} className="space-y-6">
          {/* Platform Selection */}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">
              Platform
            </label>
            <div className="grid grid-cols-2 gap-4">
              <button
                type="button"
                onClick={() => setPlatform('lichess')}
                className={`py-3 px-4 rounded-lg font-medium transition-all ${
                  platform === 'lichess'
                    ? 'bg-chess-accent text-white shadow-md'
                    : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
                }`}
              >
                Lichess
              </button>
              <button
                type="button"
                onClick={() => setPlatform('chesscom')}
                className={`py-3 px-4 rounded-lg font-medium transition-all ${
                  platform === 'chesscom'
                    ? 'bg-chess-accent text-white shadow-md'
                    : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
                }`}
              >
                Chess.com
              </button>
            </div>
          </div>

          {/* Username Input */}
          <div>
            <label htmlFor="username" className="block text-sm font-medium text-gray-700 mb-2">
              Username
            </label>
            <input
              type="text"
              id="username"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              placeholder={platform === 'lichess' ? 'hikaru' : 'magnuscarlsen'}
              className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-chess-accent focus:border-transparent outline-none transition"
            />
          </div>

          {/* Game Count Slider */}
          <div>
            <label htmlFor="gameCount" className="block text-sm font-medium text-gray-700 mb-2">
              Number of Games to Analyze: <span className="font-bold">{gameCount}</span>
            </label>
            <input
              type="range"
              id="gameCount"
              min="10"
              max="200"
              step="10"
              value={gameCount}
              onChange={(e) => setGameCount(Number(e.target.value))}
              className="w-full h-2 bg-gray-200 rounded-lg appearance-none cursor-pointer accent-chess-accent"
            />
            <div className="flex justify-between text-xs text-gray-500 mt-1">
              <span>10 games</span>
              <span>200 games</span>
            </div>
          </div>

          {/* Submit Button */}
          <button
            type="submit"
            className="w-full bg-chess-accent hover:bg-green-700 text-white font-bold py-4 px-6 rounded-lg transition-colors shadow-lg hover:shadow-xl transform hover:-translate-y-0.5"
          >
            Analyze My Games
          </button>
        </form>

        <div className="mt-6 p-4 bg-blue-50 rounded-lg">
          <p className="text-sm text-blue-800">
            <strong>💡 Tip:</strong> Analysis may take 1-3 minutes depending on the number of games.
            We'll analyze your games with Stockfish and provide AI insights with Claude.
          </p>
        </div>
      </div>
    </div>
  )
}

export default UsernameForm
