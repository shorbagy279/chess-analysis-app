import { PieChart, Pie, Cell, ResponsiveContainer, Legend, Tooltip } from 'recharts'

function OpeningsChart({ openings }) {
  const COLORS = ['#769656', '#3B7C2B', '#2D5F1E', '#1F4212', '#8FBC8F', '#6B8E23']

  const data = Object.entries(openings)
    .sort(([, a], [, b]) => b - a)
    .slice(0, 6)
    .map(([name, value]) => ({
      name: name.length > 30 ? name.substring(0, 30) + '...' : name,
      value,
      fullName: name,
    }))

  return (
    <div className="bg-white rounded-xl shadow-lg p-6">
      <h3 className="text-xl font-bold text-gray-900 mb-6">Opening Repertoire</h3>
      
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <div>
          <ResponsiveContainer width="100%" height={300}>
            <PieChart>
              <Pie
                data={data}
                cx="50%"
                cy="50%"
                labelLine={false}
                label={(entry) => `${entry.value}`}
                outerRadius={100}
                fill="#8884d8"
                dataKey="value"
              >
                {data.map((entry, index) => (
                  <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
                ))}
              </Pie>
              <Tooltip />
            </PieChart>
          </ResponsiveContainer>
        </div>

        <div className="space-y-3">
          <h4 className="text-sm font-medium text-gray-700 mb-3">Top Openings</h4>
          {data.map((opening, index) => (
            <div key={index} className="flex items-center justify-between p-3 bg-gray-50 rounded-lg">
              <div className="flex items-center space-x-3">
                <div
                  className="w-4 h-4 rounded"
                  style={{ backgroundColor: COLORS[index % COLORS.length] }}
                ></div>
                <span className="text-sm text-gray-700">{opening.name}</span>
              </div>
              <span className="font-semibold text-gray-900">{opening.value}</span>
            </div>
          ))}
        </div>
      </div>
    </div>
  )
}

export default OpeningsChart
