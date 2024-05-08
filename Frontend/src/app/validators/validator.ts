import { AbstractControl, ValidationErrors } from '@angular/forms';

export function validatorRole(control: AbstractControl): ValidationErrors | null {
  if (control.value === 'Admin' || control.value === 'User' || control.value === 'Moderator') {
    return null;
  }
  return { invalidRole: true };
}
