'use client'

import { useState } from 'react'
import { Button } from '@/components/ui/button'
import { Card } from '@/components/ui/card'

export function QualiteRiskAssessmentForm() {
  const [probability, setProbability] = useState(2)
  const [severity, setSeverity] = useState(3)
  const [mitigations, setMitigations] = useState<string[]>(['Safety training', 'Equipment inspection'])
  const [customMitigation, setCustomMitigation] = useState('')

  const riskLevel = probability * severity
  const getRiskColor = (level: number) => {
    if (level >= 20) return 'danger'
    if (level >= 10) return 'warning'
    if (level >= 5) return 'success'
    return 'success'
  }

  const getRiskLabel = (level: number) => {
    if (level >= 20) return 'Critical'
    if (level >= 10) return 'High'
    if (level >= 5) return 'Medium'
    return 'Low'
  }

  const handleAddMitigation = () => {
    if (customMitigation && !mitigations.includes(customMitigation)) {
      setMitigations([...mitigations, customMitigation])
      setCustomMitigation('')
    }
  }

  const handleRemoveMitigation = (item: string) => {
    setMitigations(mitigations.filter((m) => m !== item))
  }

  return (
    <div className="max-w-4xl mx-auto space-y-6">
      <div className="grid grid-cols-3 gap-4">
        {/* Left Panel - Action Details */}
        <Card className="col-span-1 p-4 h-fit sticky top-24">
          <h3 className="font-semibold text-foreground mb-4">Action Details</h3>
          <div className="space-y-3 text-sm">
            <div>
              <p className="text-muted-foreground">Action ID</p>
              <p className="font-medium text-foreground">ACT-001</p>
            </div>
            <div>
              <p className="text-muted-foreground">Title</p>
              <p className="font-medium text-foreground text-xs">Install preventive maintenance</p>
            </div>
            <div>
              <p className="text-muted-foreground">Owner</p>
              <p className="font-medium text-foreground">John Smith</p>
            </div>
          </div>
        </Card>

        {/* Right Panel - Risk Assessment */}
        <div className="col-span-2 space-y-6">
          {/* Risk Matrix */}
          <Card className="p-6">
            <h3 className="font-semibold text-foreground mb-6">Risk Assessment Matrix</h3>
            <div className="space-y-6">
              <div>
                <label className="block text-sm font-medium text-foreground mb-3">
                  Probability of Recurrence: {probability} / 5
                </label>
                <input
                  type="range"
                  min="1"
                  max="5"
                  value={probability}
                  onChange={(e) => setProbability(parseInt(e.target.value))}
                  className="w-full h-2 bg-muted rounded-lg appearance-none cursor-pointer accent-primary"
                />
                <div className="flex justify-between text-xs text-muted-foreground mt-2">
                  <span>Unlikely</span>
                  <span>Very Likely</span>
                </div>
              </div>

              <div>
                <label className="block text-sm font-medium text-foreground mb-3">
                  Severity of Consequences: {severity} / 5
                </label>
                <input
                  type="range"
                  min="1"
                  max="5"
                  value={severity}
                  onChange={(e) => setSeverity(parseInt(e.target.value))}
                  className="w-full h-2 bg-muted rounded-lg appearance-none cursor-pointer accent-primary"
                />
                <div className="flex justify-between text-xs text-muted-foreground mt-2">
                  <span>Minor</span>
                  <span>Catastrophic</span>
                </div>
              </div>
            </div>

            {/* Risk Score Display */}
            <div className={`mt-6 p-4 rounded-lg bg-${getRiskColor(riskLevel)}/10 border border-${getRiskColor(riskLevel)}/20`}>
              <div className="flex items-center justify-between">
                <div>
                  <p className="text-sm text-muted-foreground">Overall Risk Level</p>
                  <p className={`text-3xl font-bold text-${getRiskColor(riskLevel)}`}>
                    {getRiskLabel(riskLevel)}
                  </p>
                </div>
                <div className={`text-6xl font-bold text-${getRiskColor(riskLevel)}`}>
                  {riskLevel}
                </div>
              </div>
            </div>
          </Card>

          {/* Mitigations */}
          <Card className="p-6">
            <h3 className="font-semibold text-foreground mb-4">Mitigation Measures</h3>
            <div className="space-y-3 mb-4">
              {mitigations.map((item) => (
                <div
                  key={item}
                  className="flex items-center justify-between p-3 bg-muted/30 rounded-lg"
                >
                  <div className="flex items-center gap-3">
                    <input type="checkbox" defaultChecked className="cursor-pointer" />
                    <span className="text-foreground">{item}</span>
                  </div>
                  <button
                    onClick={() => handleRemoveMitigation(item)}
                    className="text-danger hover:text-danger/70"
                  >
                    ✕
                  </button>
                </div>
              ))}
            </div>

            <div className="flex gap-2">
              <input
                type="text"
                value={customMitigation}
                onChange={(e) => setCustomMitigation(e.target.value)}
                onKeyPress={(e) => e.key === 'Enter' && handleAddMitigation()}
                placeholder="Add new mitigation measure..."
                className="flex-1 px-4 py-2 border border-border rounded-lg bg-background text-foreground focus:outline-none focus:ring-2 focus:ring-primary"
              />
              <Button onClick={handleAddMitigation} className="bg-primary text-primary-foreground hover:bg-primary/90">
                Add
              </Button>
            </div>
          </Card>

          {/* Recommendations */}
          <Card className="p-6 bg-warning/5 border border-warning/20">
            <h3 className="font-semibold text-foreground mb-2">Risk Recommendations</h3>
            {riskLevel >= 20 && (
              <p className="text-sm text-warning">
                Critical risk identified. Immediate action required. Consider additional controls or expedited implementation.
              </p>
            )}
            {riskLevel >= 10 && riskLevel < 20 && (
              <p className="text-sm text-warning">
                High risk. Recommend enhanced monitoring and additional mitigation measures.
              </p>
            )}
            {riskLevel < 10 && (
              <p className="text-sm text-success">
                Risk level acceptable with proposed mitigation measures.
              </p>
            )}
          </Card>

          {/* Action Buttons */}
          <div className="flex gap-3">
            <Button variant="outline">Save Draft</Button>
            <Button className="bg-primary text-primary-foreground hover:bg-primary/90">
              Complete Risk Assessment
            </Button>
          </div>
        </div>
      </div>
    </div>
  )
}
