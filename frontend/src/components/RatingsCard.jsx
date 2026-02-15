import { Star } from 'lucide-react'

function RatingsCard({ tacticalRating, positionalRating, endgameRating, timeManagementRating }) {
  const ratings = [
    { name: 'Tactical Ability', value: tacticalRating },
    { name: 'Positional Play', value: positionalRating },
    { name: 'Endgame Technique', value: endgameRating },
    { name: 'Time Management', value: timeManagementRating },
  ]

  const getRatingColor = (rating) => {
    switch (rating) {
      case 'Excellent':
        return 'bg-green-500'
      case 'Good':
        return 'bg-blue-500'
      case 'Average':
        return 'bg-yellow-500'
      case 'Needs Work':
        return 'bg-red-500'
      default:
        return 'bg-gray-400'
    }
  }

  const getRatingStars = (rating) => {
    switch (rating) {
      case 'Excellent':
        return 5
      case 'Good':
        return 4
      case 'Average':
        return 3
      case 'Needs Work':
        return 2
      default:
        return 3
    }
  }

  return (
    <div className="bg-white rounded-xl shadow-lg p-6">
      <h3 className="text-xl font-bold text-gray-900 mb-4">Skill Ratings</h3>
      <div className="space-y-4">
        {ratings.map((rating, index) => (
          <div key={index}>
            <div className="flex items-center justify-between mb-2">
              <span className="text-sm font-medium text-gray-700">{rating.name}</span>
              <span className={`px-3 py-1 rounded-full text-xs font-semibold text-white ${getRatingColor(rating.value)}`}>
                {rating.value || 'N/A'}
              </span>
            </div>
            <div className="flex space-x-1">
              {[...Array(5)].map((_, i) => (
                <Star
                  key={i}
                  className={`w-4 h-4 ${
                    i < getRatingStars(rating.value)
                      ? 'text-yellow-400 fill-yellow-400'
                      : 'text-gray-300'
                  }`}
                />
              ))}
            </div>
          </div>
        ))}
      </div>
    </div>
  )
}

export default RatingsCard
