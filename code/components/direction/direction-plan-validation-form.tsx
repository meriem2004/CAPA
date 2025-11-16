'use client'

import { useState } from 'react'
import { Button } from '@/components/ui/button'
import { Card } from '@/components/ui/card'
import { ChevronDown } from 'lucide-react'

export function DirectionPlanValidationForm() {
  const [activeTab, setActiveTab] = useState('analysis')
  const [isRejectionExpanded, setIsRejectionExpanded] = useState(false)
  const [decision, setDecision] = useState('approve')
  const [comments, setComments] = useState('')
  const [rejectionReasons, setRejectionReasons] = useState<string[]>([])

  const toggleRejectionReason = (reason: string) => {
    setRejectionReasons((prev) =>
      prev.includes(reason) ? prev.filter((r) => r !== reason) : [...prev, reason]
    )
  }

  const tabs = ['Analysis', 'Plan', 'Risks', 'Budget']

  return (
    <div className="max-w-6xl mx-auto space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h2 className="text-2xl font-bold text-foreground">Plan Validation</h2>
          <p className="text-sm text-muted-foreground mt-1">Review and approve CAPA-2024-001</p>
        </div>
        <div className="px-4 py-2 bg-warning/10 text-warning rounded-lg font-medium">
          Pending Review
        </div>
      </div>

      <div className="grid grid-cols-4 gap-2">
        {tabs.map((tab) => (
          <button
            key={tab}
            onClick={() => setActiveTab(tab.toLowerCase())}
            className={`px-4 py-3 rounded-lg border transition-colors text-sm font-medium ${
              activeTab === tab.toLowerCase()
                ? 'border-primary bg-primary/10 text-primary'
                : 'border-border text-foreground hover:bg-muted'
            }`}
          >
            {tab}
          </button>
        ))}
      </div>

      <div className="grid grid-cols-3 gap-4">
        {/* Left Panel - Navigation */}
        <Card className="col-span-1 p-4 h-fit sticky top-24">
          <h3 className="font-semibold text-foreground mb-4">Sections</h3>
          <div className="space-y-2">
            {['Root Cause', 'Actions', 'Risks', 'Resources'].map((section) => (
              <button
                key={section}
                className="w-full text-left px-3 py-2 rounded hover:bg-muted transition-colors text-sm text-foreground hover:font-medium"
              >
                {section}
              </button>
            ))}
          </div>
        </Card>

        {/* Right Panel - Content */}
        <div className="col-span-2 space-y-6">
          {/* Read-Only Content */}
          <Card className="p-6">
            <h3 className="text-lg font-semibold text-foreground mb-4">CAPA Details</h3>
            <div className="space-y-4 text-sm">
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <p className="text-muted-foreground text-xs mb-1">CAPA Number</p>
                  <p className="font-medium text-foreground">CAPA-2024-001</p>
                </div>
                <div>
                  <p className="text-muted-foreground text-xs mb-1">Status</p>
                  <p className="font-medium text-warning">Submitted for Approval</p>
                </div>
                <div>
                  <p className="text-muted-foreground text-xs mb-1">Department</p>
                  <p className="font-medium text-foreground">Manufacturing</p>
                </div>
                <div>
                  <p className="text-muted-foreground text-xs mb-1">Priority</p>
                  <p className="font-medium text-danger">High</p>
                </div>
              </div>
            </div>
          </Card>

          {/* Root Cause Analysis */}
          <Card className="p-6 bg-muted/30">
            <h3 className="font-semibold text-foreground mb-4">Root Cause Analysis</h3>
            <p className="text-sm text-foreground leading-relaxed">
              Through 5 Whys analysis, we identified that the root cause is inadequate preventive maintenance schedule resulting in unexpected equipment failure. The lack of predictive maintenance monitoring allows issues to develop undetected.
            </p>
          </Card>

          {/* Proposed Actions */}
          <Card className="p-6 bg-muted/30">
            <h3 className="font-semibold text-foreground mb-4">Proposed Actions</h3>
            <div className="space-y-3">
              {['Install automated monitoring system', 'Implement weekly inspection protocol', 'Train operators on early warning signs'].map((action, i) => (
                <div key={i} className="flex items-start gap-3">
                  <span className="text-primary font-bold">{i + 1}.</span>
                  <span className="text-sm text-foreground">{action}</span>
                </div>
              ))}
            </div>
          </Card>

          {/* Decision Panel */}
          <Card className="p-6 border-2 border-primary/20">
            <h3 className="text-lg font-semibold text-foreground mb-6">Decision</h3>

            <div className="space-y-4 mb-6">
              <div className="flex items-center gap-3">
                <input
                  type="radio"
                  id="approve"
                  name="decision"
                  value="approve"
                  checked={decision === 'approve'}
                  onChange={(e) => setDecision(e.target.value)}
                  className="w-4 h-4 cursor-pointer"
                />
                <label htmlFor="approve" className="flex-1 cursor-pointer">
                  <p className="font-semibold text-foreground">Approve Plan</p>
                  <p className="text-sm text-muted-foreground">Proceed with implementation</p>
                </label>
              </div>

              <div className="flex items-start gap-3">
                <input
                  type="radio"
                  id="reject"
                  name="decision"
                  value="reject"
                  checked={decision === 'reject'}
                  onChange={(e) => setDecision(e.target.value)}
                  className="w-4 h-4 cursor-pointer mt-1"
                />
                <label htmlFor="reject" className="flex-1 cursor-pointer">
                  <p className="font-semibold text-foreground">Reject Plan</p>
                  <p className="text-sm text-muted-foreground">Request revisions</p>
                </label>
              </div>
            </div>

            {/* Rejection Form */}
            {decision === 'reject' && (
              <div className="space-y-4 pb-4 border-t">
                <div>
                  <h4 className="font-semibold text-foreground mb-3 mt-4">Rejection Reasons</h4>
                  <div className="space-y-2">
                    {['Insufficient root cause analysis', 'Budget exceeds limits', 'Timeline not realistic', 'Resource availability'].map((reason) => (
                      <label key={reason} className="flex items-center gap-2 cursor-pointer">
                        <input
                          type="checkbox"
                          checked={rejectionReasons.includes(reason)}
                          onChange={() => toggleRejectionReason(reason)}
                          className="w-4 h-4 cursor-pointer"
                        />
                        <span className="text-sm text-foreground">{reason}</span>
                      </label>
                    ))}
                  </div>
                </div>

                <div>
                  <label className="block text-sm font-semibold text-foreground mb-2">
                    Additional Comments
                  </label>
                  <textarea
                    value={comments}
                    onChange={(e) => setComments(e.target.value)}
                    placeholder="Explain what needs to be revised..."
                    rows={3}
                    className="w-full px-4 py-2 border border-border rounded-lg bg-background text-foreground focus:outline-none focus:ring-2 focus:ring-danger resize-none"
                  />
                </div>
              </div>
            )}

            {/* Approval Form */}
            {decision === 'approve' && (
              <div className="space-y-4 pb-4 border-t pt-4">
                <div>
                  <label className="block text-sm font-semibold text-foreground mb-2">
                    Approval Comments (Optional)
                  </label>
                  <textarea
                    value={comments}
                    onChange={(e) => setComments(e.target.value)}
                    placeholder="Add any comments..."
                    rows={2}
                    className="w-full px-4 py-2 border border-border rounded-lg bg-background text-foreground focus:outline-none focus:ring-2 focus:ring-primary resize-none"
                  />
                </div>
              </div>
            )}
          </Card>

          {/* Action Buttons */}
          <div className="flex gap-3">
            {decision === 'approve' ? (
              <>
                <Button variant="outline">Cancel</Button>
                <Button className="bg-success text-white hover:bg-success/90">
                  Approve CAPA Plan
                </Button>
              </>
            ) : (
              <>
                <Button variant="outline">Cancel</Button>
                <Button className="bg-danger text-white hover:bg-danger/90">
                  Send for Revision
                </Button>
              </>
            )}
          </div>
        </div>
      </div>
    </div>
  )
}
