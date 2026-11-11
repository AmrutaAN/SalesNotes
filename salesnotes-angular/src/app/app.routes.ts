import { Routes } from '@angular/router';
import { LoginComponent } from './components/login/login.component';
import { DashboardComponent } from './components/dashboard/dashboard.component';
import { NoteListComponent } from './components/note-list/note-list.component';
import { NoteDetailComponent } from './components/note-detail/note-detail.component';
import { NoteFormComponent } from './components/note-form/note-form.component';
import { CustomerListComponent } from './components/customer-list/customer-list.component';
import { RoleManagerComponent } from './components/role-manager/role-manager.component';
import { UserManagerComponent } from './components/user-manager/user-manager.component';
import { authGuard } from '@common/guards/auth.guard';
import { roleGuard } from '@common/guards/role.guard';

export const routes: Routes = [
  // Default application load page on startup is Login Page
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  { path: 'login', component: LoginComponent },

  // Protected Application Routes (Protected via authGuard: entering system is login-only)
  { path: 'dashboard', component: DashboardComponent, canActivate: [authGuard] },
  { path: 'notes', component: NoteListComponent, canActivate: [authGuard] },
  { path: 'notes/new', component: NoteFormComponent, canActivate: [authGuard] },
  { path: 'notes/:id', component: NoteDetailComponent, canActivate: [authGuard] },
  { path: 'notes/:id/edit', component: NoteFormComponent, canActivate: [authGuard] },
  { path: 'customers', component: CustomerListComponent, canActivate: [authGuard] },

  // Role-Protected Feature Routes (Features provided strictly as per user's assigned role)
  {
    path: 'users',
    component: UserManagerComponent,
    canActivate: [authGuard, roleGuard],
    data: { roles: ['SuperUser', 'Sysadmin', 'companyadmin'] }
  },
  {
    path: 'roles',
    component: RoleManagerComponent,
    canActivate: [authGuard, roleGuard],
    data: { roles: ['SuperUser', 'Sysadmin'] }
  },

  // Fallback to login
  { path: '**', redirectTo: 'login' }
];
