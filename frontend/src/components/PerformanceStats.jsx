import { BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer } from 'recharts'

function PerformanceStats({ stats, performanceByTimeControl }) {
  // Prepare data for chart
  const chartData = Object.entries(performanceByTimeControl || {}).map(([timeControl, count]) => ({
    name: timeControl,
    games: count,
  }))

  return (
    <div className="bg-white rounded-xl shadow-lg p-6">
      <h3 className="text-xl font-bold text-gray-900 mb-6">Performance Breakdown</h3>
      
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-6">
        <div className="text-center p-4 bg-red-50 rounded-lg">
          <p className="text-sm text-gray-600 mb-1">Total Blunders</p>
          <p className="text-3xl font-bold text-red-600">{stats.totalBlunders}</p>
          <p className="text-xs text-gray-500 mt-1">
            {stats.avgBlundersPerGame.toFixed(1)} per game
          </p>
        </div>
        
        <div className="text-center p-4 bg-orange-50 rounded-lg">
          <p className="text-sm text-gray-600 mb-1">Total Mistakes</p>
          <p className="text-3xl font-bold text-orange-600">{stats.totalMistakes}</p>
          <p className="text-xs text-gray-500 mt-1">
            {stats.avgMistakesPerGame.toFixed(1)} per game
          </p>
        </div>
        
        <div className="text-center p-4 bg-yellow-50 rounded-lg">
          <p className="text-sm text-gray-600 mb-1">Total Inaccuracies</p>
          <p className="text-3xl font-bold text-yellow-600">{stats.totalInaccuracies}</p>
          <p className="text-xs text-gray-500 mt-1">Per game average</p>
        </div>
      </div>

      {chartData.length > 0 && (
        <div>
          <h4 className="text-sm font-medium text-gray-700 mb-4">Games by Time Control</h4>
          <ResponsiveContainer width="100%" height={200}>
            <BarChart data={chartData}>
              <CartesianGrid strokeDasharray="3 3" />
              <XAxis dataKey="name" />
              <YAxis />
              <Tooltip />
              <Bar dataKey="games" fill="#769656" />
            </BarChart>
          </ResponsiveContainer>
        </div>
      )}
    </div>
  )
}

export default PerformanceStats
