import { Component, OnInit, inject } from '@angular/core';
import { RoleService } from '../../services/role.service';
import { AuthService } from '../../services/auth.service';
import { RoleItem } from '../../models/auth.model';

@Component({
  template: '',
  standalone: true
})
export class CommonRoleManagerComponent implements OnInit {
  protected roleService = inject(RoleService);
  protected authService = inject(AuthService);

  roles: RoleItem[] = [];
  loading = false;
  saving = false;
  errorMessage: string | null = null;
  successMessage: string | null = null;

  // Modal / Form state
  showForm = false;
  isEditing = false;
  currentRoleId: string | null = null;
  roleCode = '';
  roleName = '';
  roleDescription = '';

  ngOnInit(): void {
    this.loadRoles();
  }

  loadRoles(): void {
    this.loading = true;
    this.errorMessage = null;
    this.roleService.getRoles().subscribe({
      next: (data) => {
        this.roles = data || [];
        this.loading = false;
      },
      error: (err) => {
        console.error('Error loading roles:', err);
        this.errorMessage = 'Could not load roles from server.';
        this.loading = false;
      }
    });
  }

  openCreate(): void {
    this.isEditing = false;
    this.currentRoleId = null;
    this.roleCode = '';
    this.roleName = '';
    this.roleDescription = '';
    this.errorMessage = null;
    this.successMessage = null;
    this.showForm = true;
  }

  openEdit(role: RoleItem): void {
    this.isEditing = true;
    this.currentRoleId = role.id;
    this.roleCode = role.code;
    this.roleName = role.name;
    this.roleDescription = role.description || '';
    this.errorMessage = null;
    this.successMessage = null;
    this.showForm = true;
  }

  closeForm(): void {
    this.showForm = false;
    this.isEditing = false;
    this.currentRoleId = null;
  }

  saveRole(): void {
    if (!this.roleCode.trim() || !this.roleName.trim()) {
      this.errorMessage = 'Role code and name are required';
      return;
    }

    this.saving = true;
    this.errorMessage = null;
    this.successMessage = null;

    const payload = {
      code: this.roleCode.trim(),
      name: this.roleName.trim(),
      description: this.roleDescription.trim() || undefined
    };

    if (this.isEditing && this.currentRoleId) {
      this.roleService.updateRole(this.currentRoleId, payload).subscribe({
        next: (updated) => {
          this.saving = false;
          this.successMessage = `Role '${updated.name}' updated successfully!`;
          this.closeForm();
          this.loadRoles();
        },
        error: (err) => {
          this.saving = false;
          this.errorMessage = err.error?.message || 'Failed to update role.';
        }
      });
    } else {
      this.roleService.createRole(payload).subscribe({
        next: (created) => {
          this.saving = false;
          this.successMessage = `New role '${created.name}' created successfully!`;
          this.closeForm();
          this.loadRoles();
        },
        error: (err) => {
          this.saving = false;
          this.errorMessage = err.error?.message || 'Failed to create role. Ensure role code is unique.';
        }
      });
    }
  }

  deleteRole(role: RoleItem): void {
    if (role.isSystem) {
      alert(`System role '${role.name}' cannot be deleted.`);
      return;
    }

    if (confirm(`Are you sure you want to delete role '${role.name}' (${role.code})?`)) {
      this.loading = true;
      this.roleService.deleteRole(role.id).subscribe({
        next: () => {
          this.successMessage = `Role '${role.name}' deleted successfully.`;
          this.loadRoles();
        },
        error: (err) => {
          this.loading = false;
          alert(err.error?.message || `Failed to delete role '${role.name}'. Check if users are assigned to it.`);
        }
      });
    }
  }
}
