import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogActions, MatDialogClose, MatDialogContent, MatDialogRef, MatDialogTitle } from '@angular/material/dialog';
@Component({
  selector: 'app-error',
  templateUrl: './error.component.html',
  styleUrl: './error.component.css',
  standalone: true,
  imports:[MatDialogTitle, MatDialogContent, MatDialogActions, MatDialogClose]
})
export class ErrorComponent {
  constructor(@Inject(MAT_DIALOG_DATA) public data: { message: string },  public dialogRef: MatDialogRef<ErrorComponent>) {}


  closeDialog(): void {
    this.dialogRef.close();
  }
}
