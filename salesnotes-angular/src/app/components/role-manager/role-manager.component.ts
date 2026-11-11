import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { CommonRoleManagerComponent } from '@common/components/common-role-manager/common-role-manager.component';

@Component({
  selector: 'app-role-manager',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './role-manager.component.html',
  styleUrls: ['./role-manager.component.scss']
})
export class RoleManagerComponent extends CommonRoleManagerComponent {
  isSystemRole(code: string): boolean {
    if (!code) return false;
    const sys = ['SuperUser', 'Sysadmin', 'companyadmin', 'companyemployee'];
    return sys.some(s => s.toLowerCase() === code.trim().toLowerCase());
  }
}
