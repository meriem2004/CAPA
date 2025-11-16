'use client'

import { useState } from 'react'
import { Button } from '@/components/ui/button'
import { Card } from '@/components/ui/card'

export function QualiteRcaForm() {
  const [rcaMethod, setRcaMethod] = useState('5-whys')
  const [rootCauses, setRootCauses] = useState('')
  const [teamMembers, setTeamMembers] = useState<string[]>([])
  const [memberInput, setMemberInput] = useState('')
  const [whys, setWhys] = useState(Array(5).fill(''))

  const handleAddMember = () => {
    if (memberInput && !teamMembers.includes(memberInput)) {
      setTeamMembers([...teamMembers, memberInput])
      setMemberInput('')
    }
  }

  const handleRemoveMember = (member: string) => {
    setTeamMembers(teamMembers.filter((m) => m !== member))
  }

  const handleWhyChange = (index: number, value: string) => {
    const newWhys = [...whys]
    newWhys[index] = value
    setWhys(newWhys)
  }

  return (
    <div className="max-w-4xl mx-auto space-y-6">
      <div className="grid grid-cols-3 gap-4">
        {/* Left Panel - CAPA Context */}
        <Card className="col-span-1 p-4 h-fit sticky top-24">
          <h3 className="font-semibold text-foreground mb-4">CAPA Context</h3>
          <div className="space-y-3 text-sm">
            <div>
              <p className="text-muted-foreground">CAPA Number</p>
              <p className="font-medium text-foreground">CAPA-2024-001</p>
            </div>
            <div>
              <p className="text-muted-foreground">Status</p>
              <p className="font-medium text-warning">In Progress</p>
            </div>
            <div>
              <p className="text-muted-foreground">Department</p>
              <p className="font-medium text-foreground">Manufacturing</p>
            </div>
            <div>
              <p className="text-muted-foreground">Assigned To</p>
              <p className="font-medium text-foreground">John Smith</p>
            </div>
          </div>
        </Card>

        {/* Right Panel - RCA Analysis */}
        <div className="col-span-2 space-y-6">
          {/* SLA Indicator */}
          <Card className="p-4 border border-warning/20 bg-warning/5">
            <p className="text-sm text-muted-foreground mb-2">SLA Remaining</p>
            <div className="flex items-center justify-between">
              <div className="flex-1 h-2 bg-muted rounded-full overflow-hidden mr-4">
                <div className="h-full bg-warning w-4/5" />
              </div>
              <span className="font-semibold text-warning">5 days left</span>
            </div>
          </Card>

          {/* Method Selector */}
          <div>
            <label className="block text-sm font-semibold text-foreground mb-3">
              Root Cause Analysis Method
            </label>
            <div className="grid grid-cols-3 gap-2">
              {['5-whys', 'fishbone', 'fault-tree'].map((method) => (
                <button
                  key={method}
                  onClick={() => setRcaMethod(method)}
                  className={`py-3 px-4 rounded-lg border-2 transition-all text-sm font-medium ${
                    rcaMethod === method
                      ? 'border-primary bg-primary/10 text-primary'
                      : 'border-border text-foreground hover:border-primary/50'
                  }`}
                >
                  {method === '5-whys' && '5 Whys'}
                  {method === 'fishbone' && 'Fishbone'}
                  {method === 'fault-tree' && 'Fault Tree'}
                </button>
              ))}
            </div>
          </div>

          {/* 5 Whys Template */}
          {rcaMethod === '5-whys' && (
            <div className="space-y-4">
              <h3 className="font-semibold text-foreground">5 Whys Analysis</h3>
              {whys.map((why, index) => (
                <div key={index}>
                  <label className="block text-sm font-medium text-foreground mb-2">
                    Why #{index + 1}
                  </label>
                  <textarea
                    value={why}
                    onChange={(e) => handleWhyChange(index, e.target.value)}
                    placeholder={`Why did this happen? (${index === 0 ? 'Start here' : 'Dig deeper'})`}
                    rows={2}
                    className="w-full px-4 py-2 border border-border rounded-lg bg-background text-foreground focus:outline-none focus:ring-2 focus:ring-primary resize-none"
                  />
                </div>
              ))}
            </div>
          )}

          {/* Root Causes */}
          <div>
            <label className="block text-sm font-semibold text-foreground mb-2">
              Identified Root Causes
            </label>
            <textarea
              value={rootCauses}
              onChange={(e) => setRootCauses(e.target.value)}
              placeholder="Based on your analysis, what are the root causes?"
              rows={4}
              className="w-full px-4 py-3 border border-border rounded-lg bg-background text-foreground focus:outline-none focus:ring-2 focus:ring-primary resize-none"
            />
          </div>

          {/* Team Members */}
          <div>
            <label className="block text-sm font-semibold text-foreground mb-2">
              Team Members Involved
            </label>
            <div className="flex gap-2 mb-3">
              <input
                type="text"
                value={memberInput}
                onChange={(e) => setMemberInput(e.target.value)}
                onKeyPress={(e) => e.key === 'Enter' && handleAddMember()}
                placeholder="Type name and press Enter..."
                className="flex-1 px-4 py-2 border border-border rounded-lg bg-background text-foreground focus:outline-none focus:ring-2 focus:ring-primary"
              />
              <Button onClick={handleAddMember} className="bg-primary text-primary-foreground hover:bg-primary/90">
                Add
              </Button>
            </div>
            <div className="flex flex-wrap gap-2">
              {teamMembers.map((member) => (
                <div
                  key={member}
                  className="bg-primary/10 text-primary px-3 py-1 rounded-full flex items-center gap-2 text-sm"
                >
                  {member}
                  <button
                    onClick={() => handleRemoveMember(member)}
                    className="hover:text-primary/70 font-bold"
                  >
                    ✕
                  </button>
                </div>
              ))}
            </div>
          </div>

          {/* Action Buttons */}
          <div className="flex gap-3 pt-4">
            <Button variant="outline">Save Draft</Button>
            <Button className="bg-primary text-primary-foreground hover:bg-primary/90">
              Complete RCA
            </Button>
          </div>
        </div>
      </div>
    </div>
  )
}
