import React from 'react';
import UsernameForm from '../components/UsernameForm';
import AnalysisDashboard from '../components/AnalysisDashboard';
import { ArrowLeft } from 'lucide-react';

export default function UserAnalysisMode({ onBack }) {
  const [analysisData, setAnalysisData] = React.useState(null);
  const [loading, setLoading] = React.useState(false);

  const handleAnalysisComplete = (data) => {
    setAnalysisData(data);
    setLoading(false);
  };

  const handleAnalysisStart = () => {
    setLoading(true);
  };

  const handleReset = () => {
    setAnalysisData(null);
    setLoading(false);
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-indigo-500 via-purple-500 to-pink-500 p-8">
      <div className="max-w-7xl mx-auto">
        {/* Header */}
        <div className="flex items-center justify-between mb-8">
          <button
            onClick={onBack}
            className="flex items-center gap-2 text-white hover:text-gray-200 transition-colors"
          >
            <ArrowLeft size={24} />
            <span className="font-semibold">Back to Modes</span>
          </button>
          
          {analysisData && (
            <button
              onClick={handleReset}
              className="bg-white text-purple-600 px-6 py-2 rounded-lg font-semibold hover:bg-gray-100 transition-colors"
            >
              New Analysis
            </button>
          )}
        </div>

        {/* Title */}
        <div className="text-center mb-12">
          <h1 className="text-5xl font-bold text-white mb-4">
            User Analysis
          </h1>
          <p className="text-xl text-white/90">
            Get comprehensive insights across all your recent games
          </p>
        </div>

        {/* Content */}
        {!analysisData && !loading && (
          <UsernameForm 
            onAnalysisComplete={handleAnalysisComplete}
            onAnalysisStart={handleAnalysisStart}
          />
        )}

        {loading && (
          <div className="flex flex-col items-center justify-center py-20">
            <div className="loading-spinner mb-4"></div>
            <p className="text-white text-lg">Analyzing your games...</p>
            <p className="text-white/80 text-sm mt-2">This may take 1-3 minutes</p>
          </div>
        )}

        {analysisData && !loading && (
          <AnalysisDashboard data={analysisData} />
        )}
      </div>
    </div>
  );
}