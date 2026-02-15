import { TrendingUp, TrendingDown, AlertCircle, Lightbulb, BookOpen } from 'lucide-react'

function AIInsights({ strengths, weaknesses, commonMistakes, recommendations, openingAnalysis }) {
  const sections = [
    {
      title: 'Strengths',
      content: strengths,
      icon: TrendingUp,
      color: 'text-green-600',
      bgColor: 'bg-green-50',
      borderColor: 'border-green-200',
    },
    {
      title: 'Areas for Improvement',
      content: weaknesses,
      icon: TrendingDown,
      color: 'text-orange-600',
      bgColor: 'bg-orange-50',
      borderColor: 'border-orange-200',
    },
    {
      title: 'Common Mistakes',
      content: commonMistakes,
      icon: AlertCircle,
      color: 'text-red-600',
      bgColor: 'bg-red-50',
      borderColor: 'border-red-200',
    },
    {
      title: 'Recommendations',
      content: recommendations,
      icon: Lightbulb,
      color: 'text-blue-600',
      bgColor: 'bg-blue-50',
      borderColor: 'border-blue-200',
    },
    {
      title: 'Opening Analysis',
      content: openingAnalysis,
      icon: BookOpen,
      color: 'text-purple-600',
      bgColor: 'bg-purple-50',
      borderColor: 'border-purple-200',
    },
  ]

  return (
    <div className="space-y-4">
      <h2 className="text-2xl font-bold text-gray-900 mb-4">AI-Powered Insights</h2>
      
      {sections.map((section, index) => (
        section.content && (
          <div
            key={index}
            className={`bg-white rounded-xl shadow-lg p-6 border-l-4 ${section.borderColor}`}
          >
            <div className="flex items-center mb-3">
              <div className={`${section.bgColor} p-2 rounded-lg mr-3`}>
                <section.icon className={`w-5 h-5 ${section.color}`} />
              </div>
              <h3 className="text-lg font-bold text-gray-900">{section.title}</h3>
            </div>
            <div className="prose prose-sm max-w-none text-gray-700 whitespace-pre-line">
              {section.content}
            </div>
          </div>
        )
      ))}
    </div>
  )
}

export default AIInsights
