import { Swords } from 'lucide-react'

function PlayingStyleCard({ playingStyle }) {
  return (
    <div className="bg-gradient-to-br from-purple-500 to-purple-700 rounded-xl shadow-lg p-6 text-white">
      <div className="flex items-center mb-4">
        <Swords className="w-6 h-6 mr-2" />
        <h3 className="text-xl font-bold">Your Playing Style</h3>
      </div>
      <p className="text-purple-100 leading-relaxed">
        {playingStyle || 'Analysis in progress...'}
      </p>
    </div>
  )
}

export default PlayingStyleCard
