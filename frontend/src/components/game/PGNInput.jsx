import React, { useState } from 'react';
import { FileText, Loader2 } from 'lucide-react';

const EXAMPLE_PGN = `[Event "Live Chess"]
[Site "Chess.com"]
[Date "2024.01.15"]
[White "Player1"]
[Black "Player2"]
[Result "1-0"]
[ECO "C42"]
[Opening "Petrov Defense"]

1. e4 e5 2. Nf3 Nf6 3. Nxe5 d6 4. Nf3 Nxe4 5. d4 d5 6. Bd3 Bd6 
7. O-O O-O 8. c4 c6 9. cxd5 cxd5 10. Nc3 Nxc3 11. bxc3 Bg4 
12. Rb1 Nd7 13. Rxb7 Nf6 14. Bb2 h6 15. h3 Bh5 16. c4 Rc8 
17. cxd5 Nxd5 18. Qa4 Bg6 19. Bxg6 fxg6 20. Qxd7 Qxd7 21. Rxd7 1-0`;

export default function PGNInput({ onSubmit, loading }) {
  const [pgn, setPgn] = useState('');
  const [error, setError] = useState('');

  const handleSubmit = (e) => {
    e.preventDefault();
    
    if (!pgn.trim()) {
      setError('Please enter a PGN');
      return;
    }

    // Basic PGN validation
    if (!pgn.includes('1.') && !pgn.includes('[Event')) {
      setError('Invalid PGN format. Please enter a valid chess game in PGN notation');
      return;
    }

    setError('');
    onSubmit(pgn);
  };

  const loadExample = () => {
    setPgn(EXAMPLE_PGN);
    setError('');
  };

  return (
    <div className="max-w-4xl mx-auto">
      <div className="bg-white rounded-2xl shadow-2xl p-8">
        <div className="flex items-center gap-3 mb-6">
          <FileText className="text-purple-600" size={32} />
          <h2 className="text-2xl font-bold text-gray-800">Enter PGN</h2>
        </div>

        <form onSubmit={handleSubmit} className="space-y-6">
          <div>
            <label className="block text-sm font-semibold text-gray-700 mb-2">
              Paste your chess game in PGN format
            </label>
            <textarea
              value={pgn}
              onChange={(e) => setPgn(e.target.value)}
              placeholder="Paste PGN here..."
              className="pgn-textarea"
              disabled={loading}
            />
            {error && (
              <p className="mt-2 text-sm text-red-600">{error}</p>
            )}
          </div>

          <div className="flex gap-4">
            <button
              type="submit"
              disabled={loading}
              className="flex-1 bg-gradient-to-r from-purple-600 to-indigo-600 text-white py-3 px-6 rounded-lg font-semibold hover:from-purple-700 hover:to-indigo-700 transition-all disabled:opacity-50 disabled:cursor-not-allowed flex items-center justify-center gap-2"
            >
              {loading ? (
                <>
                  <Loader2 className="animate-spin" size={20} />
                  Analyzing...
                </>
              ) : (
                'Analyze Game'
              )}
            </button>

            <button
              type="button"
              onClick={loadExample}
              disabled={loading}
              className="bg-gray-200 text-gray-700 py-3 px-6 rounded-lg font-semibold hover:bg-gray-300 transition-colors disabled:opacity-50"
            >
              Load Example
            </button>
          </div>
        </form>

        <div className="mt-8 p-4 bg-blue-50 rounded-lg border border-blue-200">
          <h3 className="text-sm font-semibold text-blue-900 mb-2">💡 Where to find PGN:</h3>
          <ul className="text-sm text-blue-800 space-y-1">
            <li>• <strong>Chess.com:</strong> Go to your game → Share → PGN</li>
            <li>• <strong>Lichess:</strong> Go to your game → Share & Export → PGN</li>
            <li>• <strong>Format:</strong> Should include moves like "1. e4 e5 2. Nf3 Nf6..."</li>
          </ul>
        </div>
      </div>
    </div>
  );
}
