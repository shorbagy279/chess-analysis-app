function LoadingScreen() {
  return (
    <div className="max-w-2xl mx-auto">
      <div className="bg-white rounded-xl shadow-lg p-12">
        <div className="text-center">
          <div className="loading-spinner mx-auto mb-6"></div>
          
          <h2 className="text-2xl font-bold text-gray-900 mb-4">
            Analyzing Your Games...
          </h2>
          
          <div className="space-y-3 text-left max-w-md mx-auto">
            <div className="flex items-center text-gray-600">
              <div className="w-2 h-2 bg-chess-accent rounded-full mr-3 animate-pulse"></div>
              <span>Fetching games from platform...</span>
            </div>
            <div className="flex items-center text-gray-600">
              <div className="w-2 h-2 bg-chess-accent rounded-full mr-3 animate-pulse" style={{ animationDelay: '0.2s' }}></div>
              <span>Running Stockfish engine analysis...</span>
            </div>
            <div className="flex items-center text-gray-600">
              <div className="w-2 h-2 bg-chess-accent rounded-full mr-3 animate-pulse" style={{ animationDelay: '0.4s' }}></div>
              <span>Generating AI insights with Claude...</span>
            </div>
            <div className="flex items-center text-gray-600">
              <div className="w-2 h-2 bg-chess-accent rounded-full mr-3 animate-pulse" style={{ animationDelay: '0.6s' }}></div>
              <span>Preparing your personalized report...</span>
            </div>
          </div>

          <p className="mt-8 text-sm text-gray-500">
            This may take 1-3 minutes. Please don't close this page.
          </p>
        </div>
      </div>
    </div>
  )
}

export default LoadingScreen
