function LoadingScreen({ mode = 'user' }) {
  const userSteps = [
    'Fetching games from platform...',
    'Running Stockfish engine analysis...',
    'Generating AI insights with Groq...',
    'Preparing your personalized report...'
  ]

  const gameSteps = [
    'Parsing PGN notation...',
    'Analyzing positions with Stockfish...',
    'Identifying critical moments...',
    'Generating AI insights...'
  ]

  const steps = mode === 'user' ? userSteps : gameSteps

  return (
    <div className="max-w-2xl mx-auto">
      <div className="bg-white rounded-xl shadow-lg p-12">
        <div className="text-center">
          <div className="loading-spinner mx-auto mb-6"></div>
          
          <h2 className="text-2xl font-bold text-gray-900 mb-4">
            {mode === 'user' ? 'Analyzing Your Games...' : 'Analyzing Game...'}
          </h2>
          
          <div className="space-y-3 text-left max-w-md mx-auto">
            {steps.map((step, index) => (
              <div key={index} className="flex items-center text-gray-600">
                <div 
                  className="w-2 h-2 bg-green-600 rounded-full mr-3 animate-pulse" 
                  style={{ animationDelay: `${index * 0.2}s` }}
                ></div>
                <span>{step}</span>
              </div>
            ))}
          </div>

          <p className="mt-8 text-sm text-gray-500">
            This may take {mode === 'user' ? '1-3 minutes' : '30-60 seconds'}. Please don't close this page.
          </p>
        </div>
      </div>
    </div>
  )
}

export default LoadingScreen