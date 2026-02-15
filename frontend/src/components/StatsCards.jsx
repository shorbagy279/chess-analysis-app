import { Trophy, TrendingUp, AlertTriangle, Target } from 'lucide-react'

function StatsCards({ stats }) {
  const winRate = ((stats.wins / stats.totalGames) * 100).toFixed(1)
  
  const cards = [
    {
      title: 'Win Rate',
      value: `${winRate}%`,
      subtitle: `${stats.wins}W / ${stats.losses}L / ${stats.draws}D`,
      icon: Trophy,
      color: 'bg-green-500',
    },
    {
      title: 'Average Accuracy',
      value: `${stats.averageAccuracy.toFixed(1)}%`,
      subtitle: 'Overall move quality',
      icon: Target,
      color: 'bg-blue-500',
    },
    {
      title: 'Blunders Per Game',
      value: stats.avgBlundersPerGame.toFixed(1),
      subtitle: `${stats.totalBlunders} total`,
      icon: AlertTriangle,
      color: 'bg-red-500',
    },
    {
      title: 'Mistakes Per Game',
      value: stats.avgMistakesPerGame.toFixed(1),
      subtitle: `${stats.totalMistakes} total`,
      icon: TrendingUp,
      color: 'bg-orange-500',
    },
  ]

  return (
    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
      {cards.map((card, index) => (
        <div key={index} className="bg-white rounded-xl shadow-lg p-6">
          <div className="flex items-center justify-between mb-4">
            <div className={`${card.color} p-3 rounded-lg`}>
              <card.icon className="w-6 h-6 text-white" />
            </div>
          </div>
          <h3 className="text-gray-500 text-sm font-medium mb-1">{card.title}</h3>
          <p className="text-3xl font-bold text-gray-900 mb-1">{card.value}</p>
          <p className="text-gray-400 text-sm">{card.subtitle}</p>
        </div>
      ))}
    </div>
  )
}

export default StatsCards
