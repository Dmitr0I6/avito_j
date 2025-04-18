import { useForm } from "react-hook-form";
import { yupResolver } from "@hookform/resolvers/yup";
import * as yup from "yup";
import axios from "axios";
import { useState } from "react";

const schema = yup.object().shape({
    title: yup.string().required("Название обязательно"),
    description: yup.string().required("Описание обязательно"),
    category: yup.number().required("Категория обязательна").positive(),
    price: yup.number().required("Цена обязательна").positive(),
    userId: yup.number().required("ID пользователя обязательно").positive(),
});

export default function CreateAdvertisementForm() {
    const [files, setFiles] = useState([]);
    const [isSubmitting, setIsSubmitting] = useState(false);
    const [submitSuccess, setSubmitSuccess] = useState(false);
    const [error, setError] = useState(null);

    const {
        register,
        handleSubmit,
        formState: { errors },
        reset,
    } = useForm({
        resolver: yupResolver(schema),
    });

    const handleFileChange = (e) => {
        setFiles([...e.target.files]);
    };

    const onSubmit = async (data) => {
        setIsSubmitting(true);
        setError(null);

        try {
            const formData = new FormData();
            formData.append("title", data.title);
            formData.append("description", data.description);
            formData.append("category", data.category);
            formData.append("price", data.price);
            formData.append("userId", data.userId);

            files.forEach((file) => {
                formData.append("images", file);
            });

            await axios.post("http://localhost:9000/v1/advertisments", formData, {
                headers: {
                    "Content-Type": "multipart/form-data",
                },
            });

            setSubmitSuccess(true);
            reset();
            setFiles([]);
            setTimeout(() => setSubmitSuccess(false), 3000);
        } catch (err) {
            setError(err.response?.data?.message || "Ошибка при создании объявления");
        } finally {
            setIsSubmitting(false);
        }
    };

    return (
        <div style={{
            display: 'flex',
            justifyContent: 'center',
            alignItems: 'center',
            minHeight: '100vh',
            backgroundColor: '#f5f7fa',
            padding: '20px'
        }}>
            <div style={{
                width: '100%',
                maxWidth: '600px',
                padding: '40px',
                backgroundColor: 'white',
                borderRadius: '12px',
                boxShadow: '0 5px 15px rgba(0, 0, 0, 0.08)',
                margin: '0 auto'
            }}>
                <div style={{
                    textAlign: 'center',
                    marginBottom: '30px'
                }}>
                    <h2 style={{
                        fontSize: '28px',
                        fontWeight: '600',
                        color: '#2d3748',
                        marginBottom: '10px'
                    }}>
                        Создать новое объявление
                    </h2>
                    <p style={{
                        color: '#718096',
                        fontSize: '16px'
                    }}>
                        Заполните все поля для публикации объявления
                    </p>
                </div>

                {submitSuccess && (
                    <div style={{
                        padding: '15px',
                        backgroundColor: '#f0fdf4',
                        color: '#16a34a',
                        borderRadius: '8px',
                        marginBottom: '25px',
                        textAlign: 'center',
                        fontSize: '15px'
                    }}>
                        Объявление успешно создано!
                    </div>
                )}

                {error && (
                    <div style={{
                        padding: '15px',
                        backgroundColor: '#fee2e2',
                        color: '#dc2626',
                        borderRadius: '8px',
                        marginBottom: '25px',
                        textAlign: 'center',
                        fontSize: '15px'
                    }}>
                        {error}
                    </div>
                )}

                <form onSubmit={handleSubmit(onSubmit)}>
                    <div style={{
                        display: 'grid',
                        gridTemplateColumns: '1fr 1fr',
                        gap: '20px',
                        marginBottom: '20px'
                    }}>
                        <div style={{ gridColumn: 'span 2' }}>
                            <label style={{
                                display: 'block',
                                marginBottom: '8px',
                                fontWeight: '500',
                                color: '#4a5568',
                                fontSize: '15px'
                            }}>
                                Название объявления
                            </label>
                            <input
                                type="text"
                                {...register("title")}
                                style={{
                                    width: '100%',
                                    padding: '14px',
                                    border: '1px solid #e2e8f0',
                                    borderRadius: '8px',
                                    fontSize: '15px',
                                    boxSizing: 'border-box',
                                    transition: 'border-color 0.2s',
                                    outline: 'none'
                                }}
                                onFocus={(e) => e.target.style.borderColor = '#3182ce'}
                                onBlur={(e) => e.target.style.borderColor = '#e2e8f0'}
                            />
                            {errors.title && (
                                <p style={{ color: '#dc2626', fontSize: '14px', marginTop: '4px' }}>
                                    {errors.title.message}
                                </p>
                            )}
                        </div>

                        <div style={{ gridColumn: 'span 2' }}>
                            <label style={{
                                display: 'block',
                                marginBottom: '8px',
                                fontWeight: '500',
                                color: '#4a5568',
                                fontSize: '15px'
                            }}>
                                Описание
                            </label>
                            <textarea
                                {...register("description")}
                                style={{
                                    width: '100%',
                                    padding: '14px',
                                    border: '1px solid #e2e8f0',
                                    borderRadius: '8px',
                                    fontSize: '15px',
                                    boxSizing: 'border-box',
                                    transition: 'border-color 0.2s',
                                    outline: 'none',
                                    minHeight: '120px'
                                }}
                                onFocus={(e) => e.target.style.borderColor = '#3182ce'}
                                onBlur={(e) => e.target.style.borderColor = '#e2e8f0'}
                            />
                            {errors.description && (
                                <p style={{ color: '#dc2626', fontSize: '14px', marginTop: '4px' }}>
                                    {errors.description.message}
                                </p>
                            )}
                        </div>

                        <div>
                            <label style={{
                                display: 'block',
                                marginBottom: '8px',
                                fontWeight: '500',
                                color: '#4a5568',
                                fontSize: '15px'
                            }}>
                                Категория (ID)
                            </label>
                            <input
                                type="number"
                                {...register("category")}
                                style={{
                                    width: '100%',
                                    padding: '14px',
                                    border: '1px solid #e2e8f0',
                                    borderRadius: '8px',
                                    fontSize: '15px',
                                    boxSizing: 'border-box',
                                    transition: 'border-color 0.2s',
                                    outline: 'none'
                                }}
                                onFocus={(e) => e.target.style.borderColor = '#3182ce'}
                                onBlur={(e) => e.target.style.borderColor = '#e2e8f0'}
                            />
                            {errors.category && (
                                <p style={{ color: '#dc2626', fontSize: '14px', marginTop: '4px' }}>
                                    {errors.category.message}
                                </p>
                            )}
                        </div>

                        <div>
                            <label style={{
                                display: 'block',
                                marginBottom: '8px',
                                fontWeight: '500',
                                color: '#4a5568',
                                fontSize: '15px'
                            }}>
                                Цена
                            </label>
                            <input
                                type="number"
                                step="0.01"
                                {...register("price")}
                                style={{
                                    width: '100%',
                                    padding: '14px',
                                    border: '1px solid #e2e8f0',
                                    borderRadius: '8px',
                                    fontSize: '15px',
                                    boxSizing: 'border-box',
                                    transition: 'border-color 0.2s',
                                    outline: 'none'
                                }}
                                onFocus={(e) => e.target.style.borderColor = '#3182ce'}
                                onBlur={(e) => e.target.style.borderColor = '#e2e8f0'}
                            />
                            {errors.price && (
                                <p style={{ color: '#dc2626', fontSize: '14px', marginTop: '4px' }}>
                                    {errors.price.message}
                                </p>
                            )}
                        </div>

                        <div style={{ gridColumn: 'span 2' }}>
                            <label style={{
                                display: 'block',
                                marginBottom: '8px',
                                fontWeight: '500',
                                color: '#4a5568',
                                fontSize: '15px'
                            }}>
                                ID пользователя
                            </label>
                            <input
                                type="number"
                                {...register("userId")}
                                style={{
                                    width: '100%',
                                    padding: '14px',
                                    border: '1px solid #e2e8f0',
                                    borderRadius: '8px',
                                    fontSize: '15px',
                                    boxSizing: 'border-box',
                                    transition: 'border-color 0.2s',
                                    outline: 'none'
                                }}
                                onFocus={(e) => e.target.style.borderColor = '#3182ce'}
                                onBlur={(e) => e.target.style.borderColor = '#e2e8f0'}
                            />
                            {errors.userId && (
                                <p style={{ color: '#dc2626', fontSize: '14px', marginTop: '4px' }}>
                                    {errors.userId.message}
                                </p>
                            )}
                        </div>

                        <div style={{ gridColumn: 'span 2' }}>
                            <label style={{
                                display: 'block',
                                marginBottom: '8px',
                                fontWeight: '500',
                                color: '#4a5568',
                                fontSize: '15px'
                            }}>
                                Изображения
                            </label>
                            <input
                                type="file"
                                multiple
                                onChange={handleFileChange}
                                style={{
                                    width: '100%',
                                    padding: '14px',
                                    border: '1px solid #e2e8f0',
                                    borderRadius: '8px',
                                    fontSize: '15px',
                                    boxSizing: 'border-box',
                                    transition: 'border-color 0.2s',
                                    outline: 'none'
                                }}
                            />
                        </div>
                    </div>

                    <button
                        type="submit"
                        disabled={isSubmitting}
                        style={{
                            width: '100%',
                            padding: '16px',
                            backgroundColor: '#4299e1',
                            color: 'white',
                            border: 'none',
                            borderRadius: '8px',
                            fontSize: '16px',
                            fontWeight: '600',
                            cursor: 'pointer',
                            marginTop: '15px',
                            transition: 'background-color 0.2s, transform 0.1s'
                        }}
                        onMouseOver={(e) => !isSubmitting && (e.target.style.backgroundColor = '#3182ce')}
                        onMouseOut={(e) => !isSubmitting && (e.target.style.backgroundColor = '#4299e1')}
                        onMouseDown={(e) => !isSubmitting && (e.target.style.transform = 'scale(0.98)')}
                        onMouseUp={(e) => !isSubmitting && (e.target.style.transform = 'scale(1)')}
                    >
                        {isSubmitting ? (
                            <span>Отправка...</span>
                        ) : (
                            <span>Создать объявление</span>
                        )}
                    </button>
                </form>
            </div>
        </div>
    );
}