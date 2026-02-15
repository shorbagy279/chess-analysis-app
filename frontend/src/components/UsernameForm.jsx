import React, { useState } from 'react';
import { analyzeUser } from '../services/api';
import { Loader2, User, Hash } from 'lucide-react';

export default function UsernameForm({ onAnalysisComplete, onAnalysisStart }) {
  const [username, setUsername] = useState('');
  const [platform, setPlatform] = useState('LICHESS');
  const [numberOfGames, setNumberOfGames] = useState(50);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    if (!username.trim()) {
      setError('Please enter a username');
      return;
    }

    setError('');
    setLoading(true);
    
    if (onAnalysisStart) {
      onAnalysisStart();
    }

    try {
      const data = await analyzeUser(username.trim(), platform, numberOfGames);
      
      if (onAnalysisComplete) {
        onAnalysisComplete(data);
      }
    } catch (err) {
      setError(err.message || 'Failed to analyze games. Please check the username and try again.');
      setLoading(false);
    }
  };

  return (
    <div className="max-w-2xl mx-auto">
      <div className="bg-white rounded-2xl shadow-2xl p-8">
        <div className="flex items-center gap-3 mb-6">
          <User className="text-purple-600" size={32} />
          <h2 className="text-2xl font-bold text-gray-800">Enter Player Details</h2>
        </div>

        <form onSubmit={handleSubmit} className="space-y-6">
          {/* Platform Selection */}
          <div>
            <label className="block text-sm font-semibold text-gray-700 mb-2">
              Platform
            </label>
            <div className="grid grid-cols-2 gap-4">
              <button
                type="button"
                onClick={() => setPlatform('LICHESS')}
                className={`py-3 px-4 rounded-lg font-semibold transition-all ${
                  platform === 'LICHESS'
                    ? 'bg-purple-600 text-white shadow-lg'
                    : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
                }`}
                disabled={loading}
              >
                Lichess
              </button>
              <button
                type="button"
                onClick={() => setPlatform('CHESS_COM')}
                className={`py-3 px-4 rounded-lg font-semibold transition-all ${
                  platform === 'CHESS_COM'
                    ? 'bg-purple-600 text-white shadow-lg'
                    : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
                }`}
                disabled={loading}
              >
                Chess.com
              </button>
            </div>
          </div>

          {/* Username Input */}
          <div>
            <label className="block text-sm font-semibold text-gray-700 mb-2">
              Username
            </label>
            <input
              type="text"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              placeholder={platform === 'LICHESS' ? 'e.g., DrNykterstein' : 'e.g., Hikaru'}
              className="w-full px-4 py-3 border-2 border-gray-300 rounded-lg focus:border-purple-500 focus:outline-none transition-colors"
              disabled={loading}
            />
          </div>

          {/* Number of Games */}
          <div>
            <label className="block text-sm font-semibold text-gray-700 mb-2">
              Number of Games to Analyze: {numberOfGames}
            </label>
            <input
              type="range"
              min="10"
              max="200"
              step="10"
              value={numberOfGames}
              onChange={(e) => setNumberOfGames(parseInt(e.target.value))}
              className="w-full h-2 bg-gray-200 rounded-lg appearance-none cursor-pointer accent-purple-600"
              disabled={loading}
            />
            <div className="flex justify-between text-xs text-gray-500 mt-1">
              <span>10 games</span>
              <span>200 games</span>
            </div>
          </div>

          {/* Error Message */}
          {error && (
            <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-lg">
              {error}
            </div>
          )}

          {/* Submit Button */}
          <button
            type="submit"
            disabled={loading || !username.trim()}
            className="w-full bg-gradient-to-r from-purple-600 to-indigo-600 text-white py-3 px-6 rounded-lg font-semibold hover:from-purple-700 hover:to-indigo-700 transition-all disabled:opacity-50 disabled:cursor-not-allowed flex items-center justify-center gap-2"
          >
            {loading ? (
              <>
                <Loader2 className="animate-spin" size={20} />
                Analyzing...
              </>
            ) : (
              <>
                <Hash size={20} />
                Analyze My Games
              </>
            )}
          </button>
        </form>

        {/* Info */}
        <div className="mt-6 p-4 bg-blue-50 rounded-lg border border-blue-200">
          <p className="text-sm text-blue-800">
            <strong>💡 Tip:</strong> Analysis may take 1-3 minutes depending on the number of games.
            We'll analyze your games with Stockfish and provide AI insights with Groq.
          </p>
        </div>
      </div>
    </div>
  );
}
