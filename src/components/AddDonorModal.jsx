// ============================================================
// BLOODLINK — ADD DONOR MODAL
// ============================================================
import { useState } from 'react';
import { X, UserPlus } from 'lucide-react';
import { useApp } from '../services/AppStore';
import { useToast } from '../hooks/useToast';
import { BLOOD_GROUPS } from '../data/seedData';
import { validateRequired } from '../utils/helpers';

export default function AddDonorModal({ onClose, prefill = {} }) {
  const { actions } = useApp();
  const { toastSuccess, toastError } = useToast();
  const [form, setForm] = useState({
    name: prefill.name || '',
    bloodGroup: prefill.bloodGroup || '',
    city: prefill.city || '',
    phone: prefill.phone || '',
    availability: prefill.availability || 'available',
    lastDonation: prefill.lastDonation || '',
  });
  const [errors, setErrors] = useState({});
  const [submitting, setSubmitting] = useState(false);

  const set = (field) => (e) => {
    setForm(f => ({ ...f, [field]: e.target.value }));
    if (errors[field]) setErrors(prev => ({ ...prev, [field]: null }));
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    const errs = validateRequired(form, ['name', 'bloodGroup', 'city', 'availability']);
    if (Object.keys(errs).length > 0) { setErrors(errs); return; }

    setSubmitting(true);
    try {
      actions.addDonor(form);
      toastSuccess('Donor added', `${form.name} has been added to the demo registry.`);
      onClose();
    } catch (err) {
      toastError('Error', 'Failed to add donor.');
      setSubmitting(false);
    }
  };

  return (
    <div className="modal-overlay" role="dialog" aria-modal="true" aria-labelledby="add-donor-title">
      <div className="modal">
        <div className="modal-header">
          <div className="flex items-center gap-3">
            <UserPlus size={18} color="var(--sage)" aria-hidden="true" />
            <h2 id="add-donor-title" className="modal-title">Add New Donor</h2>
          </div>
          <button className="btn btn-icon btn-ghost" onClick={onClose} aria-label="Close modal"><X size={16} /></button>
        </div>

        <div className="modal-body">
          <p style={{ fontSize: 'var(--text-xs)', color: 'var(--text-muted)', lineHeight: 'var(--leading-relaxed)' }}>
            This adds a fictional demo record. Do not enter real personal information.
            Availability status is for demonstration purposes and does not imply medical eligibility.
          </p>

          <form id="add-donor-form" onSubmit={handleSubmit} noValidate>
            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 'var(--space-4)' }}>
              <div className="form-group" style={{ gridColumn: '1 / -1' }}>
                <label className="form-label form-label-required" htmlFor="donor-name">Full Name</label>
                <input
                  id="donor-name"
                  type="text"
                  className={`form-input${errors.name ? ' error' : ''}`}
                  value={form.name}
                  onChange={set('name')}
                  placeholder="Fictional demo name"
                  required
                  aria-describedby={errors.name ? 'donor-name-err' : undefined}
                />
                {errors.name && <span id="donor-name-err" className="form-error">{errors.name}</span>}
              </div>

              <div className="form-group">
                <label className="form-label form-label-required" htmlFor="donor-bloodGroup">Blood Group</label>
                <select
                  id="donor-bloodGroup"
                  className={`form-select${errors.bloodGroup ? ' error' : ''}`}
                  value={form.bloodGroup}
                  onChange={set('bloodGroup')}
                  required
                  aria-describedby={errors.bloodGroup ? 'donor-bg-err' : undefined}
                >
                  <option value="">Select group</option>
                  {BLOOD_GROUPS.map(g => <option key={g} value={g}>{g}</option>)}
                </select>
                {errors.bloodGroup && <span id="donor-bg-err" className="form-error">{errors.bloodGroup}</span>}
              </div>

              <div className="form-group">
                <label className="form-label form-label-required" htmlFor="donor-city">City / Area</label>
                <input
                  id="donor-city"
                  type="text"
                  className={`form-input${errors.city ? ' error' : ''}`}
                  value={form.city}
                  onChange={set('city')}
                  placeholder="e.g. Chennai"
                  required
                  aria-describedby={errors.city ? 'donor-city-err' : undefined}
                />
                {errors.city && <span id="donor-city-err" className="form-error">{errors.city}</span>}
              </div>

              <div className="form-group">
                <label className="form-label" htmlFor="donor-phone">Phone (Demo)</label>
                <input
                  id="donor-phone"
                  type="tel"
                  className="form-input"
                  value={form.phone}
                  onChange={set('phone')}
                  placeholder="+91-98400-00000"
                />
                <span className="form-helper">Used only within this demo interface</span>
              </div>

              <div className="form-group">
                <label className="form-label form-label-required" htmlFor="donor-availability">Demo Availability</label>
                <select
                  id="donor-availability"
                  className={`form-select${errors.availability ? ' error' : ''}`}
                  value={form.availability}
                  onChange={set('availability')}
                  required
                  aria-describedby={errors.availability ? 'donor-avail-err' : 'donor-avail-help'}
                >
                  <option value="available">Available</option>
                  <option value="unavailable">Unavailable</option>
                  <option value="on_leave">On Leave</option>
                </select>
                {errors.availability
                  ? <span id="donor-avail-err" className="form-error">{errors.availability}</span>
                  : <span id="donor-avail-help" className="form-helper">Demo status — not medical eligibility</span>
                }
              </div>

              <div className="form-group">
                <label className="form-label" htmlFor="donor-lastDonation">Last Donation Date</label>
                <input
                  id="donor-lastDonation"
                  type="date"
                  className="form-input"
                  value={form.lastDonation}
                  onChange={set('lastDonation')}
                  max={new Date().toISOString().split('T')[0]}
                />
              </div>
            </div>
          </form>
        </div>

        <div className="modal-footer">
          <button className="btn btn-secondary" onClick={onClose} type="button">Cancel</button>
          <button
            className="btn btn-primary"
            form="add-donor-form"
            type="submit"
            disabled={submitting}
          >
            {submitting ? <span className="spinner" aria-hidden="true" /> : <UserPlus size={15} aria-hidden="true" />}
            Add Donor
          </button>
        </div>
      </div>
    </div>
  );
}
