import StatsCards from './StatsCards'
import PlayingStyleCard from './PlayingStyleCard'
import RatingsCard from './RatingsCard'
import OpeningsChart from './OpeningsChart'
import PerformanceStats from './PerformanceStats'
import AIInsights from './AIInsights'

function AnalysisDashboard({ data }) {
  return (
    <div className="space-y-6">
      {/* User Header */}
      <div className="bg-white rounded-xl shadow-lg p-6">
        <div className="flex items-center justify-between">
          <div>
            <h2 className="text-2xl font-bold text-gray-900">
              {data.username}
            </h2>
            <p className="text-gray-500 mt-1">
              {data.platform === 'lichess' ? 'Lichess.org' : 'Chess.com'} • 
              {' '}{data.totalGamesAnalyzed} games analyzed
            </p>
          </div>
          <div className="text-sm text-gray-500">
            Analyzed on {new Date(data.generatedAt).toLocaleDateString()}
          </div>
        </div>
      </div>

      {/* Stats Overview */}
      <StatsCards stats={data.statistics} />

      {/* Main Content Grid */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Playing Style */}
        <PlayingStyleCard playingStyle={data.playingStyle} />

        {/* Ratings */}
        <RatingsCard
          tacticalRating={data.tacticalRating}
          positionalRating={data.positionalRating}
          endgameRating={data.endgameRating}
          timeManagementRating={data.timeManagementRating}
        />
      </div>

      {/* Performance Stats */}
      <PerformanceStats
        stats={data.statistics}
        performanceByTimeControl={data.performanceByTimeControl}
      />

      {/* Openings Chart */}
      {data.openingStats && Object.keys(data.openingStats).length > 0 && (
        <OpeningsChart openings={data.openingStats} />
      )}

      {/* AI Insights */}
      <AIInsights
        strengths={data.strengths}
        weaknesses={data.weaknesses}
        commonMistakes={data.commonMistakes}
        recommendations={data.recommendations}
        openingAnalysis={data.openingAnalysis}
      />
    </div>
  )
}

export default AnalysisDashboard
