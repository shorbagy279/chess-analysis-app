import { useState } from 'react'
import UsernameForm from './components/UsernameForm'
import AnalysisDashboard from './components/AnalysisDashboard'
import LoadingScreen from './components/LoadingScreen'

function App() {
  const [analysisData, setAnalysisData] = useState(null)
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState(null)

  const handleAnalysisComplete = (data) => {
    setAnalysisData(data)
    setLoading(false)
  }

  const handleAnalysisStart = () => {
    setLoading(true)
    setError(null)
    setAnalysisData(null)
  }

  const handleError = (errorMessage) => {
    setError(errorMessage)
    setLoading(false)
  }

  const handleReset = () => {
    setAnalysisData(null)
    setLoading(false)
    setError(null)
  }

  return (
    <div className="min-h-screen bg-gradient-to-br from-gray-50 to-gray-100">
      {/* Header */}
      <header className="bg-white shadow-sm">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-6">
          <div className="flex items-center justify-between">
            <div className="flex items-center space-x-3">
              <div className="text-4xl">♟️</div>
              <div>
                <h1 className="text-3xl font-bold text-gray-900">
                  Chess Analysis
                </h1>
                <p className="text-sm text-gray-500 mt-1">
                  AI-Powered Player Insights
                </p>
              </div>
            </div>
            {analysisData && (
              <button
                onClick={handleReset}
                className="px-4 py-2 bg-gray-200 hover:bg-gray-300 rounded-lg text-gray-700 font-medium transition-colors"
              >
                New Analysis
              </button>
            )}
          </div>
        </div>
      </header>

      {/* Main Content */}
      <main className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {error && (
          <div className="mb-6 bg-red-50 border border-red-200 rounded-lg p-4">
            <div className="flex items-center">
              <svg className="w-5 h-5 text-red-400 mr-2" fill="currentColor" viewBox="0 0 20 20">
                <path fillRule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zM8.707 7.293a1 1 0 00-1.414 1.414L8.586 10l-1.293 1.293a1 1 0 101.414 1.414L10 11.414l1.293 1.293a1 1 0 001.414-1.414L11.414 10l1.293-1.293a1 1 0 00-1.414-1.414L10 8.586 8.707 7.293z" clipRule="evenodd" />
              </svg>
              <p className="text-red-800">{error}</p>
            </div>
          </div>
        )}

        {!loading && !analysisData && (
          <UsernameForm
            onAnalysisStart={handleAnalysisStart}
            onAnalysisComplete={handleAnalysisComplete}
            onError={handleError}
          />
        )}

        {loading && <LoadingScreen />}

        {!loading && analysisData && (
          <AnalysisDashboard data={analysisData} />
        )}
      </main>

      {/* Footer */}
      <footer className="mt-16 border-t border-gray-200 bg-white">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-6">
          <p className="text-center text-gray-500 text-sm">
            Powered by Claude AI & Stockfish Engine
          </p>
        </div>
      </footer>
    </div>
  )
}

export default App
