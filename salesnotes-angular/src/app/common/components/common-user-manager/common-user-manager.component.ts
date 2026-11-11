import { Component, OnInit, inject } from '@angular/core';
import { UserService } from '../../services/user.service';
import { RoleService } from '../../services/role.service';
import { AuthService } from '../../services/auth.service';
import { User, UserRequest } from '../../models/user.model';
import { RoleItem } from '../../models/auth.model';

@Component({
  template: ''
})
export abstract class CommonUserManagerComponent implements OnInit {
  protected userService = inject(UserService);
  protected roleService = inject(RoleService);
  protected authService = inject(AuthService);

  users: User[] = [];
  roles: RoleItem[] = [];
  loading = false;
  saving = false;
  errorMessage = '';
  successMessage = '';

  searchTerm = '';
  selectedRoleFilter = '';

  showCreateModal = false;
  showEditModal = false;
  showNewUserPassword = false;
  selectedUser: User | null = null;

  newUserForm: {
    username: string;
    fullname: string;
    email: string;
    password?: string;
    role: string;
    active: boolean;
  } = {
    username: '',
    fullname: '',
    email: '',
    password: '',
    role: 'companyemployee',
    active: true
  };

  editForm: {
    id: string;
    username: string;
    fullname: string;
    email: string;
    role: string;
    active: boolean;
  } = {
    id: '',
    username: '',
    fullname: '',
    email: '',
    role: 'companyemployee',
    active: true
  };

  ngOnInit(): void {
    this.loadRoles();
    this.loadUsers();
  }

  loadRoles(): void {
    this.roleService.getRoles().subscribe({
      next: (list) => {
        this.roles = list;
      },
      error: (err) => {
        console.error('Failed to load roles', err);
      }
    });
  }

  loadUsers(): void {
    this.loading = true;
    this.errorMessage = '';
    this.userService.getUsers(this.searchTerm, this.selectedRoleFilter).subscribe({
      next: (list) => {
        this.users = list;
        this.loading = false;
      },
      error: (err) => {
        this.loading = false;
        this.errorMessage = err?.error?.message || 'Failed to load users';
      }
    });
  }

  onSearch(): void {
    this.loadUsers();
  }

  filterByRole(roleCode: string): void {
    this.selectedRoleFilter = roleCode;
    this.loadUsers();
  }

  toggleShowNewUserPassword(): void {
    this.showNewUserPassword = !this.showNewUserPassword;
  }

  openCreateModal(): void {
    this.newUserForm = {
      username: '',
      fullname: '',
      email: '',
      password: '',
      role: this.roles.length > 0 ? this.roles[0].code : 'companyemployee',
      active: true
    };
    this.showNewUserPassword = false;
    this.errorMessage = '';
    this.successMessage = '';
    this.showCreateModal = true;
  }

  closeCreateModal(): void {
    this.showCreateModal = false;
  }

  openEditModal(user: User): void {
    this.selectedUser = user;
    this.editForm = {
      id: user.id,
      username: user.username,
      fullname: user.fullname,
      email: user.email,
      role: user.role,
      active: user.active
    };
    this.errorMessage = '';
    this.successMessage = '';
    this.showEditModal = true;
  }

  closeEditModal(): void {
    this.showEditModal = false;
    this.selectedUser = null;
  }

  submitCreateUser(): void {
    if (!this.newUserForm.username.trim() || !this.newUserForm.fullname.trim() || !this.newUserForm.email.trim()) {
      this.errorMessage = 'Username, Full Name, and Email are required.';
      return;
    }
    if (!this.newUserForm.password || !this.newUserForm.password.trim()) {
      this.errorMessage = 'Password is required for new users.';
      return;
    }

    this.saving = true;
    this.errorMessage = '';
    const req: UserRequest = {
      username: this.newUserForm.username.trim(),
      fullname: this.newUserForm.fullname.trim(),
      email: this.newUserForm.email.trim(),
      password: this.newUserForm.password.trim(),
      role: this.newUserForm.role,
      active: this.newUserForm.active
    };

    this.userService.createUser(req).subscribe({
      next: () => {
        this.saving = false;
        this.showCreateModal = false;
        this.successMessage = `User '${req.username}' created successfully with role '${req.role}'!`;
        this.loadUsers();
      },
      error: (err) => {
        this.saving = false;
        this.errorMessage = err?.error?.message || 'Failed to create user.';
      }
    });
  }

  submitRoleAssignment(userId: string, newRole: string): void {
    this.saving = true;
    this.errorMessage = '';
    this.userService.assignRole(userId, newRole).subscribe({
      next: (updated) => {
        this.saving = false;
        this.showEditModal = false;
        this.successMessage = `Role for '${updated.username}' assigned to '${updated.role}'!`;
        this.loadUsers();
      },
      error: (err) => {
        this.saving = false;
        this.errorMessage = err?.error?.message || 'Failed to assign role.';
      }
    });
  }

  toggleActive(user: User): void {
    const targetStatus = !user.active;
    this.userService.updateStatus(user.id, targetStatus).subscribe({
      next: (updated) => {
        user.active = updated.active;
        this.successMessage = `User '${user.username}' is now ${user.active ? 'Active' : 'Inactive'}.`;
      },
      error: (err) => {
        this.errorMessage = err?.error?.message || 'Failed to update user status.';
      }
    });
  }

  deleteUser(user: User): void {
    if (this.isSelf(user)) {
      alert('You cannot delete your own account.');
      return;
    }
    if (!confirm(`Are you sure you want to permanently delete user '${user.username}'?`)) {
      return;
    }

    this.userService.deleteUser(user.id).subscribe({
      next: () => {
        this.successMessage = `User '${user.username}' deleted successfully.`;
        this.loadUsers();
      },
      error: (err) => {
        this.errorMessage = err?.error?.message || 'Failed to delete user.';
      }
    });
  }

  isSelf(user: User): boolean {
    const me = this.authService.getCurrentUser();
    return !!me && (me.username.toLowerCase() === user.username.toLowerCase() || me.id === user.id);
  }

  isSuperUser(): boolean {
    return this.authService.hasAnyRole(['SuperUser']);
  }

  canManageRoles(): boolean {
    return this.authService.hasAnyRole(['SuperUser', 'Sysadmin']);
  }
}
