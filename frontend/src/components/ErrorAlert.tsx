interface Props {
  message: string;
  onClose?: () => void;
}

export default function ErrorAlert({ message, onClose }: Props) {
  return (
    <div className="error-alert">
      <span>{message}</span>
      {onClose && (
        <button className="error-close" onClick={onClose}>
          &times;
        </button>
      )}
    </div>
  );
}
