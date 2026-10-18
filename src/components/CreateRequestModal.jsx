// ============================================================
// BLOODLINK — CREATE REQUEST MODAL (Shared component)
// ============================================================
import { useState } from 'react';
import { X, AlertCircle } from 'lucide-react';
import { useApp } from '../services/AppStore';
import { useToast } from '../hooks/useToast';
import { BLOOD_GROUPS, URGENCY_LEVELS } from '../data/seedData';
import { validateRequired, validatePositiveInt } from '../utils/helpers';

const URGENCY_LABELS = { critical: 'Critical', high: 'High', normal: 'Normal', low: 'Low' };

export default function CreateRequestModal({ onClose }) {
  const { actions } = useApp();
  const { toastSuccess, toastError } = useToast();

  const [form, setForm] = useState({
    bloodGroup: '',
    units: '',
    hospital: '',
    location: '',
    urgency: 'normal',
    notes: '',
  });
  const [errors, setErrors] = useState({});
  const [submitting, setSubmitting] = useState(false);

  const set = (field) => (e) => {
    setForm(f => ({ ...f, [field]: e.target.value }));
    if (errors[field]) setErrors(prev => ({ ...prev, [field]: null }));
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    const required = ['bloodGroup', 'units', 'hospital', 'location', 'urgency'];
    const errs = validateRequired(form, required);
    const unitsErr = validatePositiveInt(form.units, 'Units');
    if (unitsErr) errs.units = unitsErr;
    if (parseInt(form.units) > 50) errs.units = 'Demo limit: maximum 50 units per request';

    if (Object.keys(errs).length > 0) {
      setErrors(errs);
      return;
    }

    setSubmitting(true);
    try {
      actions.addRequest({
        ...form,
        units: parseInt(form.units, 10),
      });
      toastSuccess('Request created', `Emergency request has been submitted successfully.`);
      onClose();
    } catch (err) {
      toastError('Error', 'Failed to create request. Please try again.');
      setSubmitting(false);
    }
  };

  return (
    <div className="modal-overlay" role="dialog" aria-modal="true" aria-labelledby="create-req-title">
      <div className="modal">
        <div className="modal-header">
          <div className="flex items-center gap-3">
            <AlertCircle size={18} color="var(--amber)" aria-hidden="true" />
            <h2 id="create-req-title" className="modal-title">Create Emergency Request</h2>
          </div>
          <button className="btn btn-icon btn-ghost" onClick={onClose} aria-label="Close modal">
            <X size={16} />
          </button>
        </div>

        <div className="modal-body">
          <p style={{ fontSize: 'var(--text-xs)', color: 'var(--text-muted)', lineHeight: 'var(--leading-relaxed)' }}>
            This creates a demo request in the local application. No external notifications are sent.
            All data is fictional and for demonstration purposes only.
          </p>

          <form id="create-req-form" onSubmit={handleSubmit} noValidate>
            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 'var(--space-4)' }}>
              <div className="form-group">
                <label className="form-label form-label-required" htmlFor="req-bloodGroup">Blood Group</label>
                <select
                  id="req-bloodGroup"
                  className={`form-select${errors.bloodGroup ? ' error' : ''}`}
                  value={form.bloodGroup}
                  onChange={set('bloodGroup')}
                  required
                  aria-describedby={errors.bloodGroup ? 'req-bloodGroup-err' : undefined}
                >
                  <option value="">Select group</option>
                  {BLOOD_GROUPS.map(g => <option key={g} value={g}>{g}</option>)}
                </select>
                {errors.bloodGroup && <span id="req-bloodGroup-err" className="form-error">{errors.bloodGroup}</span>}
              </div>

              <div className="form-group">
                <label className="form-label form-label-required" htmlFor="req-units">Units Required</label>
                <input
                  id="req-units"
                  type="number"
                  min="1"
                  max="50"
                  className={`form-input${errors.units ? ' error' : ''}`}
                  value={form.units}
                  onChange={set('units')}
                  placeholder="e.g. 2"
                  required
                  aria-describedby={errors.units ? 'req-units-err' : undefined}
                />
                {errors.units && <span id="req-units-err" className="form-error">{errors.units}</span>}
              </div>
            </div>

            <div className="form-group">
              <label className="form-label form-label-required" htmlFor="req-hospital">Hospital / Requester</label>
              <input
                id="req-hospital"
                type="text"
                className={`form-input${errors.hospital ? ' error' : ''}`}
                value={form.hospital}
                onChange={set('hospital')}
                placeholder="e.g. Demo City Medical Centre"
                required
                aria-describedby={errors.hospital ? 'req-hospital-err' : undefined}
              />
              {errors.hospital && <span id="req-hospital-err" className="form-error">{errors.hospital}</span>}
            </div>

            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 'var(--space-4)' }}>
              <div className="form-group">
                <label className="form-label form-label-required" htmlFor="req-location">Location / City</label>
                <input
                  id="req-location"
                  type="text"
                  className={`form-input${errors.location ? ' error' : ''}`}
                  value={form.location}
                  onChange={set('location')}
                  placeholder="e.g. Chennai"
                  required
                  aria-describedby={errors.location ? 'req-location-err' : undefined}
                />
                {errors.location && <span id="req-location-err" className="form-error">{errors.location}</span>}
              </div>

              <div className="form-group">
                <label className="form-label form-label-required" htmlFor="req-urgency">Priority Level</label>
                <select
                  id="req-urgency"
                  className={`form-select${errors.urgency ? ' error' : ''}`}
                  value={form.urgency}
                  onChange={set('urgency')}
                  required
                  aria-describedby={errors.urgency ? 'req-urgency-err' : undefined}
                >
                  {URGENCY_LEVELS.map(u => <option key={u} value={u}>{URGENCY_LABELS[u]}</option>)}
                </select>
                {errors.urgency && <span id="req-urgency-err" className="form-error">{errors.urgency}</span>}
              </div>
            </div>

            <div className="form-group">
              <label className="form-label" htmlFor="req-notes">Notes (optional)</label>
              <textarea
                id="req-notes"
                className="form-textarea"
                value={form.notes}
                onChange={set('notes')}
                placeholder="Any additional context for this demo request..."
                rows={3}
              />
            </div>
          </form>
        </div>

        <div className="modal-footer">
          <button className="btn btn-secondary" onClick={onClose} type="button">
            Cancel
          </button>
          <button
            className="btn btn-primary"
            form="create-req-form"
            type="submit"
            disabled={submitting}
          >
            {submitting ? <span className="spinner" aria-hidden="true" /> : null}
            Create Request
          </button>
        </div>
      </div>
    </div>
  );
}
