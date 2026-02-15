import { useState } from 'react'
import PGNInput from '../components/game/PGNInput'
import InteractiveBoard from '../components/game/InteractiveBoard'
import LoadingScreen from '../components/LoadingScreen'
import { analysisAPI } from '../services/api'

function GameAnalysisMode() {
  const [analysisData, setAnalysisData] = useState(null)
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState(null)

  const handleAnalyze = async (pgn) => {
    setLoading(true)
    setError(null)

    try {
      const result = await analysisAPI.analyzeGame(pgn)
      setAnalysisData(result)
    } catch (err) {
      setError(err.message)
    } finally {
      setLoading(false)
    }
  }

  const handleReset = () => {
    setAnalysisData(null)
    setError(null)
  }

  return (
    <div className="space-y-6">
      {error && (
        <div className="bg-red-50 border border-red-200 rounded-lg p-4">
          <div className="flex items-center">
            <svg className="w-5 h-5 text-red-400 mr-2" fill="currentColor" viewBox="0 0 20 20">
              <path fillRule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zM8.707 7.293a1 1 0 00-1.414 1.414L8.586 10l-1.293 1.293a1 1 0 101.414 1.414L10 11.414l1.293 1.293a1 1 0 001.414-1.414L11.414 10l1.293-1.293a1 1 0 00-1.414-1.414L10 8.586 8.707 7.293z" clipRule="evenodd" />
            </svg>
            <p className="text-red-800">{error}</p>
          </div>
        </div>
      )}

      {!loading && !analysisData && (
        <PGNInput onAnalyze={handleAnalyze} />
      )}

      {loading && <LoadingScreen mode="game" />}

      {!loading && analysisData && (
        <div className="space-y-6">
          <div className="bg-white rounded-xl shadow-lg p-6">
            <div className="flex items-center justify-between mb-4">
              <div>
                <h2 className="text-2xl font-bold text-gray-900">
                  Game Analysis
                </h2>
                <p className="text-gray-500 mt-1">
                  {analysisData.white} vs {analysisData.black}
                </p>
              </div>
              <button
                onClick={handleReset}
                className="px-4 py-2 bg-gray-200 hover:bg-gray-300 rounded-lg text-gray-700 font-medium transition-colors"
              >
                New Analysis
              </button>
            </div>
          </div>

          <InteractiveBoard 
            pgn={analysisData.pgn}
            analysis={analysisData.analysis}
            gameInfo={analysisData.gameInfo}
          />
        </div>
      )}
    </div>
  )
}

export default GameAnalysisMode