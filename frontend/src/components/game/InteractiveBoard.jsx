import { useState, useEffect } from 'react'
import { Chess } from 'chess.js'
import { Chessboard } from 'react-chessboard'
import { ChevronLeft, ChevronRight, ChevronsLeft, ChevronsRight, RotateCw, Lightbulb } from 'lucide-react'

function InteractiveBoard({ pgn, analysis, gameInfo }) {
  const [game, setGame] = useState(new Chess())
  const [currentMove, setCurrentMove] = useState(0)
  const [moves, setMoves] = useState([])
  const [boardOrientation, setBoardOrientation] = useState('white')
  const [position, setPosition] = useState('start')
  const [moveSquares, setMoveSquares] = useState({})

  useEffect(() => {
    // Load the game from PGN
    const newGame = new Chess()
    try {
      newGame.loadPgn(pgn)
      const history = newGame.history({ verbose: true })
      setMoves(history)
      
      // Reset to start
      newGame.reset()
      setGame(newGame)
      setPosition(newGame.fen())
      setCurrentMove(0)
    } catch (error) {
      console.error('Error loading PGN:', error)
    }
  }, [pgn])

  const goToMove = (moveIndex) => {
    const newGame = new Chess()
    for (let i = 0; i <= moveIndex; i++) {
      if (moves[i]) {
        newGame.move(moves[i])
      }
    }
    setGame(newGame)
    setPosition(newGame.fen())
    setCurrentMove(moveIndex + 1)
    
    // Highlight last move
    if (moves[moveIndex]) {
      setMoveSquares({
        [moves[moveIndex].from]: { backgroundColor: 'rgba(255, 255, 0, 0.4)' },
        [moves[moveIndex].to]: { backgroundColor: 'rgba(255, 255, 0, 0.4)' }
      })
    }
  }

  const goToStart = () => {
    const newGame = new Chess()
    setGame(newGame)
    setPosition(newGame.fen())
    setCurrentMove(0)
    setMoveSquares({})
  }

  const goToEnd = () => {
    const newGame = new Chess()
    moves.forEach(move => newGame.move(move))
    setGame(newGame)
    setPosition(newGame.fen())
    setCurrentMove(moves.length)
    
    if (moves.length > 0) {
      const lastMove = moves[moves.length - 1]
      setMoveSquares({
        [lastMove.from]: { backgroundColor: 'rgba(255, 255, 0, 0.4)' },
        [lastMove.to]: { backgroundColor: 'rgba(255, 255, 0, 0.4)' }
      })
    }
  }

  const goToPrevious = () => {
    if (currentMove > 0) {
      goToMove(currentMove - 2)
    }
  }

  const goToNext = () => {
    if (currentMove < moves.length) {
      goToMove(currentMove)
    }
  }

  const flipBoard = () => {
    setBoardOrientation(prev => prev === 'white' ? 'black' : 'white')
  }

  const getCurrentMoveAnalysis = () => {
    if (!analysis || currentMove === 0) return null
    return analysis[currentMove - 1]
  }

  const getEvaluationBar = () => {
    const moveAnalysis = getCurrentMoveAnalysis()
    if (!moveAnalysis || !moveAnalysis.evaluation) return 50
    
    // Convert centipawn evaluation to percentage (capped at +/- 5)
    const cp = moveAnalysis.evaluation / 100
    const clamped = Math.max(-5, Math.min(5, cp))
    return ((clamped + 5) / 10) * 100
  }

  return (
    <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
      {/* Chess Board - Left/Center */}
      <div className="lg:col-span-2">
        <div className="bg-white rounded-xl shadow-lg p-6">
          {/* Board Container */}
          <div className="mb-6 relative">
            <Chessboard
              position={position}
              boardOrientation={boardOrientation}
              customSquareStyles={moveSquares}
              boardWidth={500}
              customBoardStyle={{
                borderRadius: '8px',
                boxShadow: '0 4px 6px rgba(0, 0, 0, 0.1)'
              }}
              customDarkSquareStyle={{ backgroundColor: '#769656' }}
              customLightSquareStyle={{ backgroundColor: '#eeeed2' }}
            />
            
            {/* Evaluation Bar */}
            <div className="absolute top-0 left-[-16px] w-2 h-full bg-gray-200 rounded-full overflow-hidden">
              <div 
                className="bg-gray-800 transition-all duration-300"
                style={{ height: `${100 - getEvaluationBar()}%` }}
              />
            </div>
          </div>

          {/* Controls */}
          <div className="flex items-center justify-between">
            <div className="flex items-center space-x-2">
              <button
                onClick={goToStart}
                className="p-2 bg-gray-100 hover:bg-gray-200 rounded-lg transition-colors"
                disabled={currentMove === 0}
              >
                <ChevronsLeft className="w-5 h-5" />
              </button>
              <button
                onClick={goToPrevious}
                className="p-2 bg-gray-100 hover:bg-gray-200 rounded-lg transition-colors"
                disabled={currentMove === 0}
              >
                <ChevronLeft className="w-5 h-5" />
              </button>
              <span className="px-4 py-2 bg-gray-50 rounded-lg font-mono text-sm">
                Move {currentMove} / {moves.length}
              </span>
              <button
                onClick={goToNext}
                className="p-2 bg-gray-100 hover:bg-gray-200 rounded-lg transition-colors"
                disabled={currentMove === moves.length}
              >
                <ChevronRight className="w-5 h-5" />
              </button>
              <button
                onClick={goToEnd}
                className="p-2 bg-gray-100 hover:bg-gray-200 rounded-lg transition-colors"
                disabled={currentMove === moves.length}
              >
                <ChevronsRight className="w-5 h-5" />
              </button>
            </div>

            <button
              onClick={flipBoard}
              className="p-2 bg-gray-100 hover:bg-gray-200 rounded-lg transition-colors"
              title="Flip board"
            >
              <RotateCw className="w-5 h-5" />
            </button>
          </div>

          {/* Current Move Analysis */}
          {getCurrentMoveAnalysis() && (
            <div className="mt-4 p-4 bg-blue-50 rounded-lg border border-blue-200">
              <div className="flex items-start">
                <Lightbulb className="w-5 h-5 text-blue-600 mr-2 mt-0.5" />
                <div>
                  <h4 className="font-semibold text-gray-900 mb-1">
                    Move Analysis
                  </h4>
                  <p className="text-sm text-gray-700">
                    {getCurrentMoveAnalysis().comment || 'Analyzing position...'}
                  </p>
                  {getCurrentMoveAnalysis().classification && (
                    <span className={`inline-block mt-2 px-2 py-1 rounded text-xs font-semibold ${
                      getCurrentMoveAnalysis().classification === 'blunder' ? 'bg-red-100 text-red-800' :
                      getCurrentMoveAnalysis().classification === 'mistake' ? 'bg-orange-100 text-orange-800' :
                      getCurrentMoveAnalysis().classification === 'inaccuracy' ? 'bg-yellow-100 text-yellow-800' :
                      'bg-green-100 text-green-800'
                    }`}>
                      {getCurrentMoveAnalysis().classification}
                    </span>
                  )}
                </div>
              </div>
            </div>
          )}
        </div>
      </div>

      {/* Moves List - Right Sidebar */}
      <div className="lg:col-span-1">
        <div className="bg-white rounded-xl shadow-lg p-6">
          <h3 className="text-lg font-bold text-gray-900 mb-4">Moves</h3>
          
          {/* Game Info */}
          {gameInfo && (
            <div className="mb-4 pb-4 border-b border-gray-200">
              <div className="text-sm text-gray-600 space-y-1">
                {gameInfo.event && <div><strong>Event:</strong> {gameInfo.event}</div>}
                {gameInfo.date && <div><strong>Date:</strong> {gameInfo.date}</div>}
                {gameInfo.result && <div><strong>Result:</strong> {gameInfo.result}</div>}
              </div>
            </div>
          )}

          {/* Scrollable Moves List */}
          <div className="overflow-y-auto max-h-[600px] space-y-1">
            {moves.reduce((acc, move, index) => {
              if (index % 2 === 0) {
                acc.push([move])
              } else {
                acc[acc.length - 1].push(move)
              }
              return acc
            }, []).map((movePair, pairIndex) => (
              <div key={pairIndex} className="flex items-center space-x-2 text-sm">
                <span className="text-gray-500 w-8 text-right">{pairIndex + 1}.</span>
                <button
                  onClick={() => goToMove(pairIndex * 2)}
                  className={`flex-1 px-2 py-1 text-left rounded transition-colors ${
                    currentMove === pairIndex * 2 + 1
                      ? 'bg-blue-500 text-white font-semibold'
                      : 'hover:bg-gray-100'
                  }`}
                >
                  {movePair[0].san}
                </button>
                {movePair[1] && (
                  <button
                    onClick={() => goToMove(pairIndex * 2 + 1)}
                    className={`flex-1 px-2 py-1 text-left rounded transition-colors ${
                      currentMove === pairIndex * 2 + 2
                        ? 'bg-blue-500 text-white font-semibold'
                        : 'hover:bg-gray-100'
                    }`}
                  >
                    {movePair[1].san}
                  </button>
                )}
              </div>
            ))}
          </div>
        </div>
      </div>
    </div>
  )
}

export default InteractiveBoard
